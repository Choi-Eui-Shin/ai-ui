
/**
 * 
 * @param {*} obj 
 * @returns 
 */
var disableControlVisible = function(obj) {
    if(obj === null || obj === undefined)
        return;

    obj.setControlVisible('tl', false);
    obj.setControlVisible('tr', false);
    obj.setControlVisible('br', false);
    obj.setControlVisible('bl', false);
    obj.setControlVisible('ml', false);
    obj.setControlVisible('mt', false);
    obj.setControlVisible('mr', false);
    obj.setControlVisible('mb', false);
    obj.setControlVisible('mtr', false);
}


export {
    disableControlVisible,
}