/**
 * 넘어온 값이 빈값인지 체크한다. 그리고 [], {} 도 빈값으로 처리한다.
 * 
 * @param {*} value 
 * @returns 
 */
var isEmpty = function (value) {
    if (value == "" || value == null || value == undefined || (value != null && typeof value == "object" && !Object.keys(value).length)) {
        return true
    } else {
        return false
    }
};

/**
 * 
 * @param {*} value 
 * @returns 
 */
var toDateFormat = function (value) {
    if(value == null || value == undefined || value.length < 14)
        return value;
    else
        return value.substr(0, 4) + "-" + value.substr(4, 2) + "-" + value.substr(6, 2) + " "
            + value.substr(8, 2) + ":" + value.substr(10, 2) + ":" + value.substr(12, 2); 
}

export {
    isEmpty,
    toDateFormat
}