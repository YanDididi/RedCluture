package com.moyu.redarmy.controller;


import com.corundumstudio.socketio.AuthorizationListener;
import com.moyu.redarmy.core.db.DBHelper;
import com.moyu.redarmy.core.result.Result;
import com.moyu.redarmy.core.result.ResultGenerator;
import com.moyu.redarmy.model.Company;
import com.moyu.redarmy.model.Node;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;
import com.moyu.redarmy.mappers.*;
import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
public class Demo {
    @RequestMapping(path = {"/testRest/{id}"}, method = RequestMethod.GET)
    public Result HelloGet(@PathVariable String id) {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        try {
            CompanyMapper mapper = sqlSession.getMapper(CompanyMapper.class);
            Company company = mapper.selectCompany(1);
            return ResultGenerator.success(company);
        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
//        System.out.println("hello spring boot");
//        return "hello a spring boot";
    }

    @RequestMapping(path = {"/testRest"}, method = RequestMethod.POST)
    public Result HelloPost(@RequestBody Map<String, Object> map) {
        //content-type:application/json; 传递json字符串
        return ResultGenerator.success(map);
//        System.out.println("hello spring boot");
//        return "hello a spring boot";
    }


    @RequestMapping(path = {"/test"}, method = RequestMethod.POST)
    public Result test() {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        DeviceMapper mapper = sqlSession.getMapper(DeviceMapper.class);

        String s = mapper.getLastNumber();
        if (null == s) {
            System.out.println("E000");
        } else {
            Integer i = Integer.valueOf(s.substring(1)) + 1;
            if (0 < i && i <= 9) {
                System.out.println("E" + "00" + i);
            } else if (9 < i && i <= 99) {
                System.out.println("E" + "0" + i);
            } else {
                System.out.println("E" + i);
            }

        }
        //content-type:application/json; 传递json字符串
        return ResultGenerator.success("111");
//        System.out.println("hello spring boot");
//        return "hello a spring boot";
    }

    @RequestMapping(path = "/delFile", method = RequestMethod.DELETE)
    public void delFile(@RequestParam("filePath") String filePath, @RequestParam("fileName") String fileName) {
        AuthorizationListener s;
        File file = new File(filePath + "/" + fileName);
        if (file.exists() && file.isFile())
            file.delete();
    }

    @RequestMapping(path = "/getNodeTree", method = RequestMethod.GET)
    public Result getNodeTree() {
        SqlSession sqlSession = DBHelper.getSqlSessionFacttory().openSession();
        NodeMapper mapper = sqlSession.getMapper(NodeMapper.class);
        List<Node> nodeLis = mapper.getNodeTree();
        try {

                return ResultGenerator.success(nodeLis);

        } catch (Exception e) {
            return ResultGenerator.fail(e.toString());
        } finally {
            sqlSession.close();
        }
    }



}
