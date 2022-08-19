package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contacts.Contacts;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.untils.DateUtils;
import com.bjpowernode.crm.commons.untils.HSSFUtils;
import com.bjpowernode.crm.commons.untils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        List<User> userList = userService.queryAllUsers();
        request.setAttribute("userList",userList);
        return "workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    public @ResponseBody Object saveCreateActivity(Activity activity, HttpSession session){
        User user = (User) session.getAttribute(Contacts.SESSION_USER);
//        为市场活动对象 activity 随机成功一个id , 取随机UUID , 此处做了封装
        activity.setId(UUIDUtils.getUUID());
//        为市场活动对象 activity 设置创建时间 获取当前时间 , 此处调用前面封装的格式化时间
        activity.setCreateTime(DateUtils.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());
        ReturnObject returnObject = new ReturnObject();
        try {
            if (activityService.saveCreateActivity(activity)>0)
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("保存失败 , 请重试!");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("保存失败 , 请重试!");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    public @ResponseBody Object queryActivityByConditionForPage(String name,String owner,
                                                                String startDate,String endDate,
                                                                Integer pageNo,Integer pageSize){
        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int countOfActivityByCondition = activityService.queryCountOfActivityByCondition(map);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("activityList",activityList);
        resultMap.put("totalRows",countOfActivityByCondition);
        return resultMap;
    }
    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    public @ResponseBody Object deleteActivityByids(String[] id){
        ReturnObject returnObject = new ReturnObject();
        try {
            if (activityService.deleteActivityByIds(id)>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("删除失败 , 请重试!>0");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("删除失败 , 请重试!try");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityById.do")
    public @ResponseBody Object queryActivityById(String id){
        return activityService.queryActivityById(id);
    }

    @RequestMapping("/workbench/activity/saveEditActivity.do")
    public @ResponseBody Object saveEditActivity(Activity activity, HttpSession session){
        ReturnObject returnObject = new ReturnObject();
//        手动封装参数 : 修改时间 、修改人id
        activity.setEditTime(DateUtils.formateDateTime(new Date()));
        activity.setEditBy(((User)session.getAttribute(Contacts.SESSION_USER)).getId());
        try {
            if (activityService.saveEditActivity(activity)>0)
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("修改失败 , 请重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("修改失败 , 请重试");

        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/exportAllActivitys.do")
    public void exportAllActivitys(HttpServletResponse response) throws Exception{
        List<Activity> activityList = activityService.queryAllActivitys();
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell =null;
        cell=row.createCell(0);cell.setCellValue("ID");
        cell=row.createCell(1);cell.setCellValue("所有者");
        cell=row.createCell(2);cell.setCellValue("名称");
        cell=row.createCell(3);cell.setCellValue("开始日期");
        cell=row.createCell(4);cell.setCellValue("结束日期");
        cell=row.createCell(5);cell.setCellValue("成本");
        cell=row.createCell(6);cell.setCellValue("描述");
        cell=row.createCell(7);cell.setCellValue("创建时间");
        cell=row.createCell(8);cell.setCellValue("创建者");
        cell=row.createCell(9);cell.setCellValue("修改时间");
        cell=row.createCell(10);cell.setCellValue("修改者");

//        遍历 activityList , 创建数据对象
        if (activityList!=null && activityList.size()>0){
            Activity activity = null;
            for (int i = 0; i <activityList.size(); i++){
                activity=activityList.get(i);
//            每个对象生成一行
                row=sheet.createRow(i+1);
//            每个属性生成一行
                cell=row.createCell(0);cell.setCellValue(activity.getId());
                cell=row.createCell(1);cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);cell.setCellValue(activity.getName());
                cell=row.createCell(3);cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);cell.setCellValue(activity.getCost());
                cell=row.createCell(6);cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);cell.setCellValue(activity.getEditBy());
            }
        }

//      将生成的 excel文件下载到
        response.setContentType("application/octet-stream;charest=UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        OutputStream outputStream = response.getOutputStream();

        wb.write(outputStream);
        wb.close();
        outputStream.flush();
    }

    @RequestMapping("/workbench/activity/exportActivityByIds.do")
    public void exportActivityById(String[] id,HttpServletResponse response) throws Exception{
        List<Activity> activityList = activityService.queryActivityByIds(id);
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell =null;
        cell=row.createCell(0);cell.setCellValue("ID");
        cell=row.createCell(1);cell.setCellValue("所有者");
        cell=row.createCell(2);cell.setCellValue("名称");
        cell=row.createCell(3);cell.setCellValue("开始日期");
        cell=row.createCell(4);cell.setCellValue("结束日期");
        cell=row.createCell(5);cell.setCellValue("成本");
        cell=row.createCell(6);cell.setCellValue("描述");
        cell=row.createCell(7);cell.setCellValue("创建时间");
        cell=row.createCell(8);cell.setCellValue("创建者");
        cell=row.createCell(9);cell.setCellValue("修改时间");
        cell=row.createCell(10);cell.setCellValue("修改者");

//        遍历 activityList , 创建数据对象
        if (activityList!=null && activityList.size()>0){
            Activity activity = null;
            for (int i = 0; i <activityList.size(); i++){
                activity=activityList.get(i);
//            每个对象生成一行
                row=sheet.createRow(i+1);
//            每个属性生成一行
                cell=row.createCell(0);cell.setCellValue(activity.getId());
                cell=row.createCell(1);cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);cell.setCellValue(activity.getName());
                cell=row.createCell(3);cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);cell.setCellValue(activity.getCost());
                cell=row.createCell(6);cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);cell.setCellValue(activity.getEditBy());
            }
        }
        response.setContentType("application/octet-stream;charest=UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        wb.close();
        outputStream.flush();
    }

    @RequestMapping("/workbench/activity/importActivity.do")
    public @ResponseBody Object importActivity(MultipartFile activityFile,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        User user = (User) session.getAttribute(Contacts.SESSION_USER);
        try {
//            将excel文件写到磁盘上
//            String filename = activityFile.getOriginalFilename();
//            File file = new File("F:\\Java Web\\DLJD-crm\\md",filename);
//            activityFile.transferTo(file);

//            解析excel文件
//            InputStream inputStream = new FileInputStream("F:\\Java Web\\DLJD-crm\\md\\" + filename);
//            得到该文件的workbook对象
            InputStream inputStream=activityFile.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
//            得到页对象
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row=null;
            HSSFCell cell=null;
            Activity activity=null;
            List<Activity> activityList = new ArrayList<>();
//            遍历该页 , getLastRowNum()函数获取最后一行的下标
            for (int i=1;i<=sheet.getLastRowNum();i++){
//                获得该行对象
                row=sheet.getRow(i);
//                遍历该行
                activity=new Activity();
//                id 、owner 、createTime、createBy自动设置
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formateDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for (int j=0;j<row.getLastCellNum();j++){
//                    获取数据 , 存入activity对象
                    cell=row.getCell(j);
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    switch (j){
                        case 0:activity.setName(cellValue);
                        case 1:activity.setStartDate(cellValue);
                        case 2:activity.setEndDate(cellValue);
                        case 3:activity.setCost(cellValue);
                        case 4:activity.setDescription(cellValue);
                    }
                }
                activityList.add(activity);
            }
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setMessage(" 成功上传 "+activityService.saveCreateActivityByList(activityList)+" 条数据 ");
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(" 文件上传失败 , 请重试 ");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id, HttpServletRequest request){
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList", remarkList);
        return "workbench/activity/detail";
    }
}
