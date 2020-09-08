$(function(){
	var listUrl = "/o2o/shopadmin/getproductcategorylist";
	var addUrl = "/o2o/shopadmin/addproductcategorys";
	var deleteUrl = "/o2o/shopadmin/removeproductcategory";
	getList();
	//1.展示商品类别列表
	function getList(){
		$.getJSON(listUrl,function(data){
			if(data.success){
				var dataList = data.data;
				$('.category-wrap').html('');
				var tempHtml = '';
				dataList.map(function(item,index){
					tempHtml += ''
						+'<div class="row row-product-category now">'
						+'<div class="col-33">' + item.productCategoryName + '</div>' //class=product-category-name
						+'<div class="col-33">' + item.priority +'</div>'
						+'<div class="col-33"><a href="#" class="button delete" data-id="' + item.productCategoryId +'">删除</a></div>'
						+'</div>';
				});
				$('.category-wrap').append(tempHtml);
			}
		});
	}
	
	//2.点击新建，添加一行
	$('#new').click(function(){
				var tempHtml = '<div class="row row-product-category temp">'
					+ '<div class="col-33"><input class="category-input category" type="text" placeholder="分类名" /></div>'
					+ '<div class="col-33"><input class="category-input priority" type="number" placeholder="优先级" /></div>'
					+ '<div class="col-33"><a href="#" class="button delete">删除</a></div>'
					+'</div>';
				$('.category-wrap').append(tempHtml);
			});
			
	//3.点击提交，将temp的控件遍历获取，组成列表传到后台		
	$('#submit').click(function(){
		var tempArr = $('.temp');
		var productCategoryList = [];
		tempArr.map(function(index,item){
			var tempObj = {};
			tempObj.productCategoryName = $(item).find('.category').val();
			tempObj.priority = $(item).find('.priority').val();
			if(tempObj.productCategoryName && tempObj.priority){
				productCategoryList.push(tempObj);
			}
		});
		$.ajax({
			url:addUrl,
			type:'POST',
			data:JSON.stringify(productCategoryList),
			contentType:'application/json',
			success:function(data){
				if(data.success){
					$.toast("提交成功！");
					getList();//更新后用于刷新列表
				}else{
					$.toast("提交失败");
				}
			}
		});
	});
	
	//4.点击删除未添加商品类别数据的单元行
	$('.category-wrap').on('click','.row-product-category.temp .delete',function(e){
		console.log($(this).parent().parent());
		$(this).parent().parent().remove();
	});
	
	//5.点击删除已添加商品类别数据的单元行
	$('.category-wrap').on('click','.row-product-category.now .delete',function(e){
		var target = e.currentTarget;
		$.confirm('确定删除吗？',function(){
			$.ajax({
				url:deleteUrl,
				type:'POST',
				data:{productCategoryId:target.dataset.id},
				dataType:'json',
				success:function(data){
					if(data.success){
						$.toast('删除成功！'),
						getList();
					}else{
						$.toast('删除失败！');
					}
				}
			});
		});
	});
})