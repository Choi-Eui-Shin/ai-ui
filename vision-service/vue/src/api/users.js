import net from "@/utils/net.js";

/**
 * 비밀번호를 변경한다.
 * @param {*} passwordData 
 * @returns 
 */
export function changePassword(passwordData) {
	return net.put(`/v1/users/password`, passwordData);
}

/**
 * 접속이력을 가져온다.
 * @returns 
 */
 export function getLogs() {
	return net.get(`/v1/users/log`);
}

/**
 * 비밀번호 초기화
 * @param {*} userData 
 * @returns 
 */
export function resetPassword(userData) {
	return net.put(`/v1/users/reset`, userData);
}

/**
 * 사용자 정보 변경
 * @param {*} userData 
 * @returns 
 */
export function updateUser(userData) {
	return net.put(`/v1/users`, userData);
}

/**
 * 사용자 추가
 * @param {*} userData 
 * @returns 
 */
export function addUser(userData) {
	return net.post(`/v1/users`, userData);
}

/**
 * 사용자 삭제
 * @param {*} userId 
 * @param {*} password 
 * @returns 
 */
export function deleteUser(userId, password) {
	return net.delete(`/v1/users/${userId}/${password}`);
}


/**
 * 전체 사용자 목록을 가져온다.
 * @returns 
 */
export function getUserList() {
	return net.get(`/v1/users`);
}

/**
 * 로그인 
 * @param {*} loginData 
 * @returns 
 */
export function login(loginData) {
	return net.post(`/v1/users/design/login`, loginData);
}

/**
 * 로그아웃
 * @returns
 */
export function logout() {
	return net.get(`/v1/users/design/logout`);
}

/**
 * 인증방법을 가져온다.
 * @returns 
 */
export function getAuthMethod() {
	return net.post(`/v1/users/auth`, {});
}
