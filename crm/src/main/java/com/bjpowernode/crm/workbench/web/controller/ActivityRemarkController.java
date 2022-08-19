package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contacts.Contacts;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.untils.DateUtils;
import com.bjpowernode.crm.commons.untils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class ActivityRemarkController {

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    public @ResponseBody Object saveCreateActivityRemark(ActivityRemark activityRemark, HttpSession session){
        User user = (User) session.getAttribute(Contacts.SESSION_USER);
        //        封装参数
        activityRemark.setId(UUIDUtils.getUUID());
        activityRemark.setCreateTime(DateUtils.formateDateTime(new Date()));
        activityRemark.setCreateBy(user.getId());
        activityRemark.setEditFlag(Contacts.REMARK_EDIT_FLAG_NO_EDIT);

        ReturnObject returnObject = new ReturnObject();
        try {
            int saveCreateActivityRemarkResult = activityRemarkService.saveCreateActivityRemark(activityRemark);
            if (saveCreateActivityRemarkResult>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage("保存成功");
                returnObject.setRetData(activityRemark);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("保存失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("保存失败");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    public @ResponseBody Object deleteActivityRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try {
            int deleteActivityRemarkByIdResult = activityRemarkService.deleteActivityRemarkById(id);
            if (deleteActivityRemarkByIdResult>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("删除失败 , 请重试!");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("删除失败 , 请重试!");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/saveEditActivityRemark.do")
    public @ResponseBody Object saveEditActivityRemark(ActivityRemark activityRemark,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        User user =(User) session.getAttribute(Contacts.SESSION_USER);
//        手动封装实体类
        activityRemark.setEditBy(user.getId());
        activityRemark.setEditTime(DateUtils.formateDateTime(new Date()));
        activityRemark.setEditFlag(Contacts.REMARK_EDIT_FLAG_YES_EDIT);

        try {
            int saveEditActivityRemarkResult = activityRemarkService.saveEditActivityRemark(activityRemark);
            if (saveEditActivityRemarkResult>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(activityRemark);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("更新失败 , 请重试!");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("更新失败 , 请重试!");
        }
        return returnObject;
    }
}
