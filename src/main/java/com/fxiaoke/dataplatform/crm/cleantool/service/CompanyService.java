package com.fxiaoke.dataplatform.crm.cleantool.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fxiaoke.dataplatform.crm.cleantool.dao.CompanyDao;
import com.fxiaoke.dataplatform.crm.cleantool.entity.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by weilei on 2016/10/26.
 */
@Service
@Slf4j
public class CompanyService {
    @Autowired
    private CompanyDao companyDao;


    public JSONObject getDetailsByName(String companyName) {
        List<Company> result = companyDao.getDetailsByName(companyName);
        return getResultJson(result);
    }

    public JSONObject getDetailsByKeyNo(String keyNo) {
        String id = "";
        int tabId = Integer.parseInt(keyNo.substring(keyNo.length()-2,keyNo.length()), 16);
        if(tabId<10)
            id = "0"+tabId;
        else
            id = String.valueOf(tabId);
        List<Company> result = companyDao.getDetailsByKeyNo(keyNo,id);
        return getResultJson(result);
    }

    private JSONObject getResultJson(List<Company> result){
        JSONObject resultJson = new JSONObject();
        if(result==null||result.size()==0){
            resultJson.put("Status","-1");
            resultJson.put("Message","查询失败");
        }else{
            Company info = result.get(0);
            resultJson.put("Status","200");
            resultJson.put("Message","查询成功");
            JSONObject company = new JSONObject();
            company.put("TermStart",info.getTermStart()==null?"":info.getTermStart());
            company.put("TeamEnd",info.getTeamEnd()==null?"":info.getTeamEnd());
            company.put("CheckDate",info.getCheckDate()==null?"":info.getCheckDate());
            company.put("KeyNo",info.getKeyNo()==null?"":info.getKeyNo());
            company.put("Name",info.getName()==null?"":info.getName());
            company.put("No",info.getNo()==null?"":info.getNo());
            company.put("BelongOrg",info.getBelongOrg()==null?"":info.getBelongOrg());
            company.put("OperName",info.getOperName()==null?"":info.getOperName());
            company.put("StartDate",info.getStartDate()==null?"":info.getStartDate());
            company.put("EndDate",info.getEndDate()==null?"":info.getEndDate());
            company.put("Status",info.getStatus()==null?"":info.getStatus());
            company.put("Province",info.getProvince()==null?"":info.getProvince());
            company.put("UpdatedDate",info.getUpdatedDate()==null?"":info.getUpdatedDate());
            company.put("CreditCode",info.getCreditCode()==null?"":info.getCreditCode());
            String registCapiDesc = info.getRegistCapiDesc()==null?"":info.getRegistCapiDesc();
            company.put("RegistCapi",(info.getRegistCapi()==null||"-10000".equals(info.getRegistCapi()))?"":(info.getRegistCapi()+registCapiDesc));
            company.put("EconKind",info.getEconKind()==null?"":info.getEconKind());
            company.put("Address",info.getAddress()==null?"":info.getAddress());
            company.put("Scope",info.getScope()==null?"":info.getScope());

            JSONObject contactInfo = new JSONObject();
            JSONArray webSite = new JSONArray();
            JSONObject web = new JSONObject();
            web.put("Name",info.getName()==null?"":info.getName());
            web.put("Url",info.getUrl()==null?"":info.getUrl());
            webSite.add(web);
            contactInfo.put("WebSite",webSite);
            String mobile =info.getMobile()==null?"":info.getMobile();
            contactInfo.put("PhoneNumber",(info.getPhoneNumber()==null||"".equals(info.getPhoneNumber().trim()))?mobile:info.getPhoneNumber());
            contactInfo.put("Email",info.getEmail()==null?"":info.getEmail());
            company.put("ContactInfo",contactInfo);
            resultJson.put("Result",company);
        }
        return resultJson;
    }

    public static void main(String[] args){
        String str  =  "09c64ed4c5ef0db94fa8d7ef82726b6d";
        System.out.println(Integer.parseInt(str.substring(str.length()-2,str.length()), 16));
    }
}
