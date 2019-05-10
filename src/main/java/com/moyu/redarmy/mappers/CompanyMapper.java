package com.moyu.redarmy.mappers;

import com.moyu.redarmy.model.Company;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface CompanyMapper {
    Company selectCompany(int id);
    List<Company> selectCompanys();
    //    @MapKey("id")
//    Map<Integer,Company> selectCompanys();
    int insertCompany(Company company);
    int updateCompany(Company company);
    int deleteCompany(int id);
}
