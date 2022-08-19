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
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<%--分页插件--%>
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

<script type="text/javascript">
	function queryActivityByConditionForPage(pageNo,pageSize){
		var name=$("#query-name").val();
		var owner=$("#query-owner").val();
		var startDate=$("#query-startDate").val();
		var endDate=$("#query-endDate").val();
		// var pageNo=1;
		// var pageSize=10;
		$.ajax({
			url: 'workbench/activity/queryActivityByConditionForPage.do',
			data: {
				name : name,
				owner : owner,
				startDate : startDate,
				endDate : endDate,
				pageNo : pageNo,
				pageSize : pageSize
			},
			type: 'post',
			dataType: 'json',
			success : function (data){
				//渲染获取的数据
				//数据总条数
				$("#totalRows").text(data.totalRows);
				//拼接数据
				var htmlStr="";
				$.each(data.activityList,function (index,obj){
					htmlStr+="<tr class=\"active\">";
					htmlStr+="<td><input type=\"checkbox\" value=\""+obj.id+"\"/></td>";
					htmlStr+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/detailActivity.do?id="+obj.id+"'\">"+obj.name+"</a></td>";
					htmlStr+="<td>"+obj.owner+"</td>";
					htmlStr+="<td>"+obj.startDate+"</td>";
					htmlStr+="<td>"+obj.endDate+"</td>";
					htmlStr+="</tr>";
				})
				$("#tBody").html(htmlStr);

				//查询数据后 , 取消全选按钮
				//$("#checkAll").prop("checked",false);

				//计算总页数
				var totalPages=1;
				(data.totalRows%pageSize==0)?(totalPages=data.totalRows/pageSize):totalPages=parseInt(totalPages=data.totalRows/pageSize)+1

				//调用分页插件
				$("#demo_pag1").bs_pagination({
					//当前页号
					currentPage:pageNo,
					//每页显示数据条数
					rowsPerPage:pageSize,
					//数据总数
					totalRows:data.totalRows,
					//总页数
					totalPages:totalPages,
					//最多显示的卡片数
					visiblePageLinks: 5,
					//是否显示"跳转到"部分 , 默认为true showGoToPage:
					//是否显示"每页显示条数"部分 , 默认为true showRowsPerPage:
					//是否显示记录的信息 , 默认true showRowsInfo:,

					//当数据改变时 , 触发此函数
					onChangePage : function (event,pageObj){
						queryActivityByConditionForPage(pageObj.currentPage,pageObj.rowsPerPage);
						$("#checkAll").prop("checked",false);
					}
				})
			}
		});

	};

	$(function(){
		//给创建按钮添加单击事件
		$("#createActivityBtn").click(function (){
		//	初始化表单 , 将模态窗口残留数据清除
			$("#createActivityForm").get(0).reset();
		//	弹出创建的模态窗口
			$("#createActivityModal").modal("show");

		})

		//给保存按钮添加单击事件
		$("#saveCreateActivityBtn").click(function (){
			var owner=$("#create-marketActivityOwner").val();
			var name=$.trim($("#create-marketActivityName").val());
			var startDate=$("#create-startDate").val();
			var endDate=$("#create-endDate").val();
			var cost=$.trim($("#create-cost").val());
			var description=$.trim($("#create-description").val());
			if (owner==""){
				alert("请选择所有者");
				return
			}
			if (name==""){
				alert("请输入市场活动名称");
				return
			}
			if (startDate!=""&&endDate!=""){
				if (startDate<startDate){
					alert("请输入正确的结束日期与开始日期 , 结束日期需大于开始日期")
					return
				}
			}
			var regExp=/^(([1-9]\d*)|0)$/;
			if (!regExp.test(cost)){
				alert("请输入正确的成本");
				return;
			}
			//发送请求
			$.ajax({
				url : "workbench/activity/saveCreateActivity.do",
				data : {
					owner : owner,
					name : name,
					startDate : startDate,
					endDate : endDate,
					cost : cost,
					description : description
				},
				type : 'post',
				dataType : 'json',
				success : function(data){
					if (data.code=="1"){
						$("#createActivityModal").modal("hide");
						queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination('getOption','rowsPerPage' +
								''));
					}
					else
						alert(data.message);
				}
			})
		})

		// 给日期添加日历插件
		$(".mydate").datetimepicker({
			//设置语言
			language : 'zh-CN',
			//设置日期格式
			format : 'yyyy-mm-dd',
			//最小选择视图
			minView : 'month',
			//初始化显示日期
			initialDate : new Date(),
			//选择完日期后 ， 是否自动关闭日历
			autoclose : true,
			//是否显示 ‘today’ 按钮 , 单击后会直接选中当日
			todayBtn : true,
			//是否显示 ‘清除’ 按钮 , 单击后会清除当前选中日期
			clearBtn : true
		})

		//当市场活动页面加载完成后 , 查询所有数据第一页和所有数据的总条数
		queryActivityByConditionForPage(1,5);

		//条件查询
		$("#queryActivityBtn").click(function () {
			//点击查询后不会重置每页显示个数 , 而是读取上次的每页显示个数数据
			queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));

		});

		//给全选按钮添加单击事件
		$("#checkAll").click(function (){
			//若"全选"按钮被选中 , 选中所有checkbox
			// if(this.checked){
			// 	$("#tBody input[type='checkbox']").prop("checked","true");
			// }else {
			// 	$("#tBody input[type='checkbox']").prop("checked","false");
			// }
			$("#tBody input[type='checkbox']").prop("checked",this.checked);
		})

		//当所有数据都被手动选中时 , 自动选中"全选"按钮 ; 否则只要有任何一个数据没被选中 , 则将全选按钮取消选中
		$("#tBody").on("click","input[type='checkbox']",function (){
				if ($("#tBody input[type='checkbox']").size()==$("#tBody input[type='checkbox']:checked").size()){
					$("#checkAll").prop("checked",true);
				}else {
					$("#checkAll").prop("checked",false);
				}
		});

		//删除按钮处理事件
		$("#deleteActivityBtn").click(function (){
			var checkedIds=$("#tBody input[type='checkbox']:checked");

			if (checkedIds.size()==0){
				alert("请选择要删除的市场活动");
				return
			}

			if (window.confirm("是否确认删除")){
				var ids="";
				//对checkedIds中的每个对象都执行此方法
				$.each(checkedIds,function (){
					//将数据封装为  id=xxxx&id=xxxx....的形式 , 但最终会多一个 &
					ids+="id="+this.value+"&";
				})
				//alert(ids);
				//对此字符串不取最后一个&
				ids=ids.substr(0,ids.length-1);
				$.ajax({
					url : 'workbench/activity/deleteActivityByIds.do' ,
					data : ids ,
					type : 'post',
					dataType : 'json',
					success : function (data){
						if (data.code=="1")
							queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));
						else
							alert(data.message);

					}
				})

			}
		})

		//给修改按钮添加单击事件
		$("#editActivityBtn").click(function (){
			var checkIds = $("tBody input[type='checkbox']:checked");
			if (checkIds.size()!=1){
				alert("请逐条修改");
				return
			}
			var id =checkIds[0].value;
			//alert(id);
			$.ajax({
				url : 'workbench/activity/queryActivityById.do',
				data : {
					id:id
				},
				type : 'post',
				dataType : 'json',
				success : function (data){
					$("#edit-id").val(data.id);
					$("#edit-marketActivityOwner").val(data.owner);
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startData").val(data.startDate);
					$("#edit-endData").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-description").val(data.description);

					$("#editActivityModal").modal("show");
				}
			})
		})

		//给更新按钮添加单击事件
		$("#saveEditActivityBtn").click(function (){
			var id=$("#edit-id").val();
			var owner=$("#edit-marketActivityOwner").val();
			var name=$.trim($("#edit-marketActivityName").val());
			var startDate=$("#edit-startData").val();
			var endDate=$("#edit-endData").val();
			var cost=$.trim($("#edit-cost").val());
			var description=$.trim($("#edit-description").val());
			if (owner==""){
				alert("请选择所有者");
				return
			}
			if (name==""){
				alert("请输入市场活动名称");
				return
			}
			if (startDate!=""&&endDate!=""){
				if (startDate<startDate){
					alert("请输入正确的结束日期与开始日期 , 结束日期需大于开始日期")
					return
				}
			}
			var regExp=/^(([1-9]\d*)|0)$/;
			if (!regExp.test(cost)){
				alert("请输入正确的成本");
				return;
			}
			$.ajax({
				url : 'workbench/activity/saveEditActivity.do',
				data : {
					id : id,
					owner : owner,
					name : name,
					startDate : startDate,
					endDate : endDate,
					cost : cost,
					description : description
				},
				type : 'post',
				dataType : 'json',
				success : function (data){
					if (data.code==1){
						$("#editActivityModal").modal("hide");
						queryActivityByConditionForPage($("#demo_pag1").bs_pagination('getOption','currentPage'),$("#demo_pag1").bs_pagination('getOption','rowsPerPage'))
					}else
						alert(data.message);
				}
			})
		})

		//给批量导出按钮添加单击事件
		$("#exportActivityAllBtn").click(function (){
			window.location.href="workbench/activity/exportAllActivitys.do";
		})

		//给选择导出按钮添加单击事件
		$("#exportActivityXzBtn").click(function (){
			var checkedIds=$("#tBody input[type='checkbox']:checked");

			if (checkedIds.size()==0){
				alert("请选择要导出的市场活动");
				return
			}

			var ids="";
			//对checkedIds中的每个对象都执行此方法
			$.each(checkedIds,function (){
				//将数据封装为  id=xxxx&id=xxxx....的形式 , 但最终会多一个 &
				ids+="id="+this.value+"&";
			})
			//alert(ids);
			//对此字符串不取最后一个&
			ids=ids.substr(0,ids.length-1);

			window.location.href="workbench/activity/exportActivityByIds.do?"+ids;
		})

		//给导入市场活动按钮添加单击事件
		$("#importActivityBtn").click(function (){
			var activityFileName=$("#activityFile").val();
			var suffix=activityFileName.substr(activityFileName.lastIndexOf(".")+1).toLocaleLowerCase();
			if (suffix!="xls"){
				alert("只支持excel文件 , 即后缀为 .xls 类型的文件!")
				return
			}
			var activityFile=$("#activityFile")[0].files[0];
			if (activityFile.size>5*1024*1024){
				alert("文件最大只能5MB");
				return;
			}
			var formData=new FormData();
			formData.append("activityFile",activityFile);
			$.ajax({
				url : "workbench/activity/importActivity.do",
				data : formData,
				processData : false,
				contentType : false,
				type : "post",
				dataType : "json",
				success : function (data) {
					if (data.code=="1"){
						alert(data.message);
						$("#importActivityModal").modal("hide");
						queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));
					} else {
						alert(data.message);
						$("#importActivityModal").modal("show");
					}
				}
			})
		})
	});

</script>
</head>
<body>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="createActivityForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								  <c:forEach items="${userList}" var="user">
									  <option value="${user.id}">${user.name}</option>
								  </c:forEach>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="create-startDate" readonly>
							</div>
							<label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveCreateActivityBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id"/>
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
									<c:forEach items="${userList}" var="user">
										<option value="${user.id}">${user.name}</option>
									</c:forEach>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startData" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="edit-startData" value="2020-10-10">
							</div>
							<label for="edit-endData" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="edit-endData" value="2020-10-20">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveEditActivityBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="query-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="query-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="query-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="query-endDate">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createActivityBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editActivityBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteActivityBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="checkAll"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="tBody">
<%--						<tr class="active">--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.html';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--							<td>2020-10-10</td>--%>
<%--							<td>2020-10-20</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.html';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>2020-10-10</td>--%>
<%--                            <td>2020-10-20</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
				<div id="demo_pag1"></div>
			</div>
			
<%--			<div style="height: 50px; position: relative;top: 30px;">--%>
<%--				<div>--%>
<%--					<button type="button" class="btn btn-default" style="cursor: default;">共<b id="totalRows">50</b>条记录</button>--%>
<%--				</div>--%>
<%--				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">--%>
<%--					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>--%>
<%--					<div class="btn-group">--%>
<%--						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">--%>
<%--							10--%>
<%--							<span class="caret"></span>--%>
<%--						</button>--%>
<%--						<ul class="dropdown-menu" role="menu">--%>
<%--							<li><a href="#">20</a></li>--%>
<%--							<li><a href="#">30</a></li>--%>
<%--						</ul>--%>
<%--					</div>--%>
<%--					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>--%>
<%--				</div>--%>
<%--				<div style="position: relative;top: -88px; left: 285px;">--%>
<%--					<nav>--%>
<%--						<ul class="pagination">--%>
<%--							<li class="disabled"><a href="#">首页</a></li>--%>
<%--							<li class="disabled"><a href="#">上一页</a></li>--%>
<%--							<li class="active"><a href="#">1</a></li>--%>
<%--							<li><a href="#">2</a></li>--%>
<%--							<li><a href="#">3</a></li>--%>
<%--							<li><a href="#">4</a></li>--%>
<%--							<li><a href="#">5</a></li>--%>
<%--							<li><a href="#">下一页</a></li>--%>
<%--							<li class="disabled"><a href="#">末页</a></li>--%>
<%--						</ul>--%>
<%--					</nav>--%>
<%--				</div>--%>
<%--			</div>--%>
			
		</div>
		
	</div>
</body>
</html>