package com.fxiaoke.dataplatform.crm.cleantool.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fxiaoke.dataplatform.crm.cleantool.service.CompanyService;
import com.fxiaoke.dataplatform.crm.cleantool.service.ElasticsearchService;
import com.github.jedis.support.MergeJedisCmd;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by weilei on 2016/10/27.
 */
@Slf4j
@Controller
@RequestMapping("/enterinfo")
public class CrmEnterInfoController {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private CompanyService companyService;
    @Resource(name="industryRedisSupport")
    private MergeJedisCmd jedis;

    private final String ES_INDEX = "crmenterinfo";
    private final String ES_TYPE = "idandname";
    private final Integer CRM_RESULT_CACHE = 24*60*60;   //缓存数据有效时长
    private final Integer ES_MAX_CALL = 200000;   //缓存数据有效时长
    private final Integer MYSQL_MAX_CALL = 200000;   //缓存数据有效时长

    /**
     * HIVE中存有企查查的企业名单，查出后放入ES
     */
    @RequestMapping(value="/generateCompanyIdAndNameToES")
    @ResponseBody
    public String generateCompanyIdAndNamEToES(){
        String esSql = String.format(" select company_id,company_name, oper_name," +
                " status ,regist_no from dw_bds_db_crm.tmp_company ");
        elasticsearchService.saveToEs(esSql,"crmenterinfo/idandname");
        return  "submit Spark task";
    }

    /**
     * 根据关键字获取企业名单列表
     * @param keyword
     * @param from
     * @param size
     * @param model
     * @return
     */
    @RequestMapping(value="/Search")
    @ResponseBody
    public JSONObject queryCompanyByKey(@RequestParam("keyword") String keyword,
                                        @RequestParam("pageIndex") int from,
                                        @RequestParam("pageSize") int size,
                                        @RequestParam("ei") int ei,
                                        Model model){
        String key = getKey(ei,"es");
        int count = jedis.get(key)==null?0:Integer.valueOf(jedis.get(key));
        JSONObject resultJson = new JSONObject();
        if(count>ES_MAX_CALL){
            resultJson.put("Status",201);
            resultJson.put("Result",null);
        }else{
            from = from -1;
            List<Map<String, Object>> result = elasticsearchService.search( ES_INDEX,  ES_TYPE, keyword, from,size);
            if(result==null||result.size()==0){
                resultJson.put("Status","-1");
                resultJson.put("Message","查询失败");
            }else{
                resultJson.put("Status","200");
                resultJson.put("Message","查询成功");
                JSONObject paging = new JSONObject();
                paging.put("PageSize",size);
                paging.put("PageIndex",from);
                paging.put("TotalRecords",result.size());
                resultJson.put("Paging",paging);

                JSONArray array =  new JSONArray();
                result.forEach(r ->{
                    JSONObject company = new JSONObject();
                    company.put("KeyNo",r.get("company_id")==null?"":r.get("company_id").toString());
                    company.put("Name",r.get("company_name")==null?"":r.get("company_name").toString());
                    company.put("OperName",r.get("oper_name")==null?"":r.get("oper_name").toString());
                    company.put("Status",r.get("status")==null?"":r.get("status").toString());
                    company.put("No",r.get("regist_no")==null?"":r.get("regist_no").toString());
                    array.add(company);
                });

                resultJson.put("Result",array);
                jedis.setex(key,CRM_RESULT_CACHE,String.valueOf(++count));
            }
        }
        return resultJson;
    }

    /**
     * 根据企业名称获取企业详细信息
     * @param keyword
     * @param model
     * @return
     */
    @RequestMapping(value="/GetDetailsByName")
    @ResponseBody
    public JSONObject getDetailsByName(@RequestParam("keyword") String keyword,
                                       @RequestParam("ei") int ei,
                                       Model model){
        String key = getKey(ei,"mysql");
        int count = jedis.get(key)==null?0:Integer.valueOf(jedis.get(key));
        JSONObject resultJson = new JSONObject();
        if(count>MYSQL_MAX_CALL){
            resultJson.put("Status",201);
            resultJson.put("Result",null);
        }else{
            resultJson = companyService.getDetailsByName(keyword);
            jedis.setex(key,CRM_RESULT_CACHE,String.valueOf(++count));
        }
        return  resultJson;
    }

    /**
     * 根据企业ID获取企业详细信息
     * @param keyNo
     * @param model
     * @return
     */
    @RequestMapping(value="/GetDetailsByKeyNo")
    @ResponseBody
    public JSONObject getDetailsByKeyNo(@RequestParam("KeyNo") String keyNo,
                                        @RequestParam("ei") int ei,
                                        Model model){

        String key = getKey(ei,"mysql");
        int count = jedis.get(key)==null?0:Integer.valueOf(jedis.get(key));
        JSONObject resultJson = new JSONObject();
        if(count>MYSQL_MAX_CALL){
            resultJson.put("Status",201);
            resultJson.put("Result",null);
        }else{
            resultJson = companyService.getDetailsByKeyNo(keyNo);
            jedis.setex(key,CRM_RESULT_CACHE,String.valueOf(++count));
        }
        return  resultJson;
    }


    private String getKey(int ei,String type){
       String day =  new DateTime().toString("yyyy-MM-dd");
       return ei+"_"+type+"_"+day;
    }
}
