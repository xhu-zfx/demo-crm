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
//        ????????????????????? activity ??????????????????id , ?????????UUID , ??????????????????
        activity.setId(UUIDUtils.getUUID());
//        ????????????????????? activity ?????????????????? ?????????????????? , ??????????????????????????????????????????
        activity.setCreateTime(DateUtils.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());
        ReturnObject returnObject = new ReturnObject();
        try {
            if (activityService.saveCreateActivity(activity)>0)
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("???????????? , ?????????!");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("???????????? , ?????????!");
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
                returnObject.setMessage("???????????? , ?????????!>0");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("???????????? , ?????????!try");
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
//        ?????????????????? : ???????????? ????????????id
        activity.setEditTime(DateUtils.formateDateTime(new Date()));
        activity.setEditBy(((User)session.getAttribute(Contacts.SESSION_USER)).getId());
        try {
            if (activityService.saveEditActivity(activity)>0)
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("???????????? , ?????????");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("???????????? , ?????????");

        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/exportAllActivitys.do")
    public void exportAllActivitys(HttpServletResponse response) throws Exception{
        List<Activity> activityList = activityService.queryAllActivitys();
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("??????????????????");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell =null;
        cell=row.createCell(0);cell.setCellValue("ID");
        cell=row.createCell(1);cell.setCellValue("?????????");
        cell=row.createCell(2);cell.setCellValue("??????");
        cell=row.createCell(3);cell.setCellValue("????????????");
        cell=row.createCell(4);cell.setCellValue("????????????");
        cell=row.createCell(5);cell.setCellValue("??????");
        cell=row.createCell(6);cell.setCellValue("??????");
        cell=row.createCell(7);cell.setCellValue("????????????");
        cell=row.createCell(8);cell.setCellValue("?????????");
        cell=row.createCell(9);cell.setCellValue("????????????");
        cell=row.createCell(10);cell.setCellValue("?????????");

//        ?????? activityList , ??????????????????
        if (activityList!=null && activityList.size()>0){
            Activity activity = null;
            for (int i = 0; i <activityList.size(); i++){
                activity=activityList.get(i);
//            ????????????????????????
                row=sheet.createRow(i+1);
//            ????????????????????????
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

//      ???????????? excel???????????????
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
        HSSFSheet sheet = wb.createSheet("??????????????????");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell =null;
        cell=row.createCell(0);cell.setCellValue("ID");
        cell=row.createCell(1);cell.setCellValue("?????????");
        cell=row.createCell(2);cell.setCellValue("??????");
        cell=row.createCell(3);cell.setCellValue("????????????");
        cell=row.createCell(4);cell.setCellValue("????????????");
        cell=row.createCell(5);cell.setCellValue("??????");
        cell=row.createCell(6);cell.setCellValue("??????");
        cell=row.createCell(7);cell.setCellValue("????????????");
        cell=row.createCell(8);cell.setCellValue("?????????");
        cell=row.createCell(9);cell.setCellValue("????????????");
        cell=row.createCell(10);cell.setCellValue("?????????");

//        ?????? activityList , ??????????????????
        if (activityList!=null && activityList.size()>0){
            Activity activity = null;
            for (int i = 0; i <activityList.size(); i++){
                activity=activityList.get(i);
//            ????????????????????????
                row=sheet.createRow(i+1);
//            ????????????????????????
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
//            ???excel?????????????????????
//            String filename = activityFile.getOriginalFilename();
//            File file = new File("F:\\Java Web\\DLJD-crm\\md",filename);
//            activityFile.transferTo(file);

//            ??????excel??????
//            InputStream inputStream = new FileInputStream("F:\\Java Web\\DLJD-crm\\md\\" + filename);
//            ??????????????????workbook??????
            InputStream inputStream=activityFile.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
//            ???????????????
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row=null;
            HSSFCell cell=null;
            Activity activity=null;
            List<Activity> activityList = new ArrayList<>();
//            ???????????? , getLastRowNum()?????????????????????????????????
            for (int i=1;i<=sheet.getLastRowNum();i++){
//                ??????????????????
                row=sheet.getRow(i);
//                ????????????
                activity=new Activity();
//                id ???owner ???createTime???createBy????????????
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formateDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for (int j=0;j<row.getLastCellNum();j++){
//                    ???????????? , ??????activity??????
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
            returnObject.setMessage(" ???????????? "+activityService.saveCreateActivityByList(activityList)+" ????????? ");
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(" ?????????????????? , ????????? ");
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
