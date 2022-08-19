<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		$(function (){
			$("#loginBtn").click(function(){
				var loginAct=$.trim($("#loginAct").val());
				var loginPwd=$.trim($("#loginPwd").val());
				var isRemPwd=$("#isRemPwd").prop("checked");
			//	表单验证
				if (loginAct==""){
					alert("用户名不能为空");
					return;
				}
				else if (loginPwd==""){
					alert("密码不能为空");
					return;
				}
				//异步请求
				$.ajax({
					url:'settings/qx/user/login.do',
					data:{
						loginAct:loginAct,
						loginPwd:loginPwd,
						isRemPwd:isRemPwd
					},
					type:'post',
					dataType:'json',
					success:function(data){
						if(data.code=="1"){
							window.location.href="workbench/index.do"
						}else{
							$("#msg").text(data.message);
						}

					},
					beforeSend:function(){
						// ajax 向后台发送请求之前 , 执行此函数\
						// 返回值能决定是否向后台发送请求
						// true则发送请求、false则不发送请求
						// 一般用于写前端表单验证
						$("#msg").text("正在验证");
						return true;
					}
				});
			});
			//绑定回车登录
			$(window).keydown(function(event){
				if(event.keyCode==13) {
					$("#loginBtn").click();
				}
			});
			//显示与隐藏密码
			$(function(){
				var flag=1;
				$("#showPwd").click(function(){
					if (flag==1){
						$("#loginPwd").prop("type","text");
						flag=0;
					}else{
						$("#loginPwd").prop("type","password");
						flag=1;
					}
					// ($("#loginPwd").type==="password")?($("#loginPwd").prop("type","text")):($("#loginPwd").prop("type","password"))
				});

			});
		});
	</script>

</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2019&nbsp;动力节点</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" id="loginAct" type="text" value="${cookie.loginAct.value}" placeholder="用户名">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" id="loginPwd" type="password"  value="${cookie.loginPwd.value}" placeholder="密码">
						<button id="showPwd">显示密码</button>
					</div>

					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						<label>
							<c:if test="${not empty cookie.loginAct and not empty cookie.LoginPwd}">
								<input type="checkbox" id="isRemPwd" checked>
							</c:if>
							<c:if test="${empty cookie.loginAct or empty cookie.LoginPwd}">
								<input type="checkbox" id="isRemPwd">
							</c:if>
							十天内免登录
						</label>
						&nbsp;&nbsp;
						<span id="msg" style="color:red;"></span>
					</div>
					<button type="button" id="loginBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>

		</div>
	</div>
</body>
</html>