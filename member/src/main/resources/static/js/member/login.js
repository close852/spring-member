$(document).ready(function() {
	$("#loginForm").validate({
		// debug : true,
		// highlight: function(element, errorClass) {
		// $(element).fadeOut(function() {
		// $(element).fadeIn();
		// });
		// },
		rules : {
			username : {
				required : true,
				isBlankValue : true
			},
			password : {
				required : true
			}
		},
		submitHandler : function(form) {
			form.submit();
		}
	});

	// 공통 메시지로 생성
	$.extend($.validator.messages, {
		required : "필수 정보입니다.",
		isBlankValue :"공백은 입력할 수 없습니다."
	});

	$.validator.addMethod("isBlankValue", function(value, element) {
		let blank_ = / /gi;
		value = value.replace(blank_, '');
		element.value = value;
		return value.length;
	});
});