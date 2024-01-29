var condition = document.getElementById('conditionCheckbox')
var policy = document.getElementById('privacyCheckbox')
var contbtn = document.getElementById('continuebtn')

function checkRadio(){
    contbtn.disabled = !(condition.checked && policy.checked);
}
function checkSubmit() {
    android.onEventJs('userconsent_accept');
}

function checkDismiss() {
    android.onEventJs('userconsent_dismiss');
}