/**
 * 1.从后台获取到店铺分类、区域等是信息，将它填充到前台的html空间里面，
   2.获取表单信息，将它转发到后台去注册店铺
   3.所有的动作都是异步的，不影响用户前端的体验
 */
$(function(){
	
	var shopId = getQueryString('shopId');  //从请求的URL中匹配到shopId
	var initUrl = '/o2o/shopadmin/getshopinitinfo';//获取店铺的初始信息，还未定义
	var registerShopUrl = '/o2o/shopadmin/registershop';//注册店铺
	var shopInfoUrl = '/o2o/shopadmin/getshopbyid?shopid=' + shopId;
	var editShopUrl = '/o2o/shopadmin/modifyshop';	
	//定义一个变量，用来传递状态：传递店铺，默认对店铺进行更新；不传，默认注册店铺
	var isEdit = shopId?true:false;
	if(!isEdit){
		getShopInitInfo();//没有shopId就是注册，调用初始化
	}else{
		getShopInfo(shopId);//有shopId就是修改，查询店铺信息
	}
	
	/*
	*1.有shopId就是修改，查询店铺信息
	*/
	function getShopInfo(shopId){
	$.getJSON(shopInfoUrl,function(data){
		if(data.success){
			var shop = data.shop;
			$('#shop-name').val(shop.shopName);
			$('#shop-addr').val(shop.shopAddr);
			$('#shop-phone').val(shop.phone);
			$('#shop-desc').val(shop.shopDesc);
			var shopCategory = '<option data-id="' 
				+ shop.shopCategory.shopCategoryId + '" selected>'
				+ shop.shopCategory.shopCategoryName + '</option>';
			var tempAreaHtml = '';
			data.areaList.map(function(item, index){
				tempAreaHtml += '<option data-id="' + item.areaId +'">'
				+item.areaName + '</option>';
			});
			$('#shop-category').html(shopCategory);
			$('#shop-category').attr('disabled','disabled');
			$('#area').html(tempAreaHtml);
			$('#area option[data-id="'+shop.area.areaId+'"]').attr("selected", "selected");
		}
	});
}

	/*
	*2.获取商铺分类、区域的列表信息
	*/	
	function getShopInitInfo(){
	//alert(initUrl);//调试弹窗，证明js文件被加载
		//1 访问的URL，回调方法，获取商铺分类、区域的列表信息
		$.getJSON(initUrl,function(data){ 
			if(data.success){ //true
				var tempHtml = '';  //1.1 存放店铺类别列表
				var tempAreaHtml = '';  //1.2 存放区域列表
				data.shopCategoryList.map(function(item,index){
					//1.3 用map遍历店铺类别列表，生成如<option data-id="1">盖浇饭</option>的列表
					tempHtml += '<option data-id="' + item.shopCategoryId + '">' +item.shopCategoryName + "</option>";
				});
				data.areaList.map(function(item,index){
					//1.4 用map遍历区域列表，生成如<option data-id="1">东苑</option>的列表
					tempAreaHtml += '<option data-id="' + item.areaId + '">' + item.areaName + '</option>';
				});
				//1.5 完成遍历之后，将获取到的信息，塞进前台定义好的id里面 #是css的用法，代表id
				$('#shop-category').html(tempHtml);
				$('#area').html(tempAreaHtml);
			}
		});
		
	/*
	*3. 点击提交，获取到表单信息，通过ajax转发到后台
	*/
	$('#submit').click(function(){//点击id为submit的空间，调用方法
		var shop={};//json对象
		//3.1 获取控件的信息
		if(isEdit){
			shop.shopId = shopId;
		}
		shop.shopName = $('#shop-name').val();
		shop.shopAddr = $('#shop-addr').val();
		shop.phone = $('#shop-phone').val();
		shop.shopDesc = $('#shop-desc').val();
		shop.shopCategory = {
				shopCategoryId:$('#shop-category').find('option').not(function(){//返回被选中的,双重否定表肯定
					return !this.selected;
				}).data('id')
		};
		shop.area = {
				areaId:$('#area').find('option').not(function(){
					return !this.selected;
				}).data('id')
		};
		var shopImg = $('#shop-img')[0].files[0];
		//3.2 定义表单，用于接收
		var formData = new FormData();
		formData.append('shopImg',shopImg);
		formData.append('shopStr',JSON.stringify(shop));//将json转换成字符流
		//3.3 接收验证码
		var verifyCodeActual = $('#j_kaptcha').val();
		if(!verifyCodeActual){
			$.toast('请输入验证码！');
			return;
		}
		formData.append('verifyCodeActual',verifyCodeActual);
		//3.4 提交表单数据
		$.ajax({
			url: (isEdit ? editShopUrl:registerShopUrl),
			type:'POST',
			data:formData,
			contentType:false,//既要传文件，又要传文字，设为false
			processData:false,
			cache:false,
			success:function(data){
				if(data.success){
					$.toast("提交成功！");
				}else{
					$.toast("提交失败："+data.errMsg);
				}
				$('#captcha_img').click()  
			}
		});
	});
	}
})