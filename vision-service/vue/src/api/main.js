import net from "@/utils/net.js";


/**
 * 템플릿 목록을 조회한다.
 * @returns 
 */
export function getTemplate() {
    return net.get(`/v1/main/template`);
}

/**
 * 지정된 정보를 이용하여 소스코드를 생성한다.
 * 
 * @param {*} payload 
 * @returns 
 */
export function generate(payload) {
    return net.post(`/v1/main/generate`, payload);
}

/**
 * UI, 백앤드 소스코드 다운로드
 * @param {*} payload 
 * @returns 
 */
export function download(downloadType,  payload) {
    return net.post(`/v1/main/${downloadType}/download`, payload, {responseType: "blob"});
}