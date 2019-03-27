$(document).ready(function() {
	$("#joinForm").validate({
		// debug : true,
		// highlight: function(element, errorClass) {
		// $(element).fadeOut(function() {
		// $(element).fadeIn();
		// });
		// },
		rules : {
			email : {
				required : true,
				isBlankValue : true,
				email : true,
				isExistUser : true
			},
			username : {
				required : true,
				isBlankValue : true
			},
			password : {
				required : true,
				isBlankValue : true,
				passwordChk : true,
				minlength : 10
			},
			passwordConfirm : {
				required : true,
				equalTo : '#password'
			}
		},
		submitHandler : function(form) {
			// 필요에 따라 ajax를 사용한 제출등으로 변경가능.
			// 여기다가 isExistUser 걸어야 되나...
			form.submit();
		}
		// 특정 메시지 생성 가능
		// ,messages : {
		// username : {
		// required : "3323"
		// }
		// }
	});

	// 공통 메시지로 생성
	$.extend($.validator.messages, {
		required : "필수 정보입니다.",
		email : "유효하지 않은 E-mail 입니다.",
		equalsTo : "비밀번호가 일치하지 않습니다.",
		minlength : $.validator.format("{0}자리 이상 입력해주세요."),
		passwordChk : "영문 대 소문자, 숫자, 특수문자를 사용하세요.",
		isExistUser : "이미 존재하는 계정입니다.",
		equalTo : "동일한 값을 입력해주세요.",
		isBlankValue : "공백은 입력할 수 없습니다."
	});

	$.validator.addMethod("passwordChk", function(value, element) {
		return this.optional(element)
		|| /^.*(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/
		.test(value);
	});
	$.validator.addMethod("isBlankValue", function(value, element) {
		let blank_ = / /gi;
		value = value.replace(blank_, '');
		element.value = value;
		return value.length;
	});

	// 디바운싱으로 해결할 방법을 찾아야 한다.
	$.validator.addMethod("isExistUser", function(value, element) {
		console.log('asd..?')
		var isc = false;
		timer = null;
		$.ajax({
			url : "/apis/member/findUserByIdAjax",
			method : "POST",
			async : false,
			data : {
				email : value
			}
		}).done(function(user) {
			console.log(user+"/....")
			if (user != '') {
				isc = false;
			} else {
				isc = true;
			}
		});
		return isc;
	});

});
//let timer = null;
//function debounce(fn, delay) {
//let context = this;
//let isc = false;
//if (timer) {
//clearTimeout(timer);
//}
////만약 이벤트가 호출되면 타이머를 초기화 하고 다시 시작한다.
//timer = setTimeout(function() {
//fn.apply();
//}, delay);
//return isc;
//}
