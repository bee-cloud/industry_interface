package com.fxiaoke.dataplatform.crm.cleantool.dao;

import com.fxiaoke.dataplatform.crm.cleantool.entity.Company;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by weilei on 2016/10/28.
 */
@Repository
public interface CompanyDao extends CrudDao<Company> {

    public List<Company> getDetailsByName(@Param("companyName") String companyName);

    public List<Company> getDetailsByKeyNo(@Param("keyNo") String keyNo);
}
