import net from "@/utils/net.js";

/**
 * 지정된 이미지를 분석하여 UI 요소를 추출한다.
 * 
 * @param {*} imageFile 
 * @returns 
 */
export function yolov8(imageFile) {
	var fd = new FormData();
    fd.append('file', imageFile);

    return net.post(`/v1/vision/predict`, fd, {
        timeout: 300000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}
