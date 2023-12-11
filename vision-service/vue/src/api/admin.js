import net from "@/utils/net.js";

/**
 * 소스에서 추출한 BO 정보를 저장한다.
 * 
 * @param {*} jsonFile 
 * @returns 
 */
export function importBO(jsonFile) {
    var fd = new FormData();
    fd.append('file', jsonFile);

    return net.post(`/v1/tables/bo`, fd, {
        timeout: 300000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * ER-WIN에서 저장한 XML 파일에서 추출한 관계(Relationship)와 스키마를 입력
 * @param {*} erdZip ERWIN에서 추출한 JSON 파일을 압축한 파일
 * @returns 
 */
export function importERDZip(erdZip) {
    var fd = new FormData();
    fd.append('jar', erdZip);

    return net.post(`/v1/tables/erwinZip`, fd, {
        timeout: 600000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * ER-WIN에서 저장한 XML 파일에서 추출한 관계(Relationship)와 스키마를 입력
 * @param {*} erdJson 
 * @param {*} tableJson 
 * @param {*} columnJson 
 * @returns 
 */
export function importERDV2(erdJson, tableJson, columnJson) {
    var fd = new FormData();
    fd.append('erdJson', erdJson);
    fd.append('tableJson', tableJson);
    fd.append('columnJson', columnJson);

    return net.post(`/v1/tables/erwin`, fd, {
        timeout: 300000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 
 * @param {*} jsonFile 
 * @param {*} target 
 * @returns 
 */
export function importNLPModel(jsonFile, target) {
    var fd = new FormData();
    fd.append('file', jsonFile);

    return net.post(`/v1/model/${target}`, fd, {
        timeout: 120000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 지정된 타입의 템플릿을 삭제한다.
 * @param {*} designType 
 */
export function clearTemplate(designType) {
    return net.delete(`/v1/design/template/${designType}`);
}

/**
 * 템플릿 정보를 읽어 들인다.
 * (주) returnCode가 false이며, returnMessage가 공백인 경우 데이터가 없는 것으로 판단한다.
 * @returns 
 */
export function getTemplage() {
    return net.get(`/v1/design/template`);
}

/**
 * 엑셀 템플릿을 등록한다.
 * @param {*} flagNew 
 * @param {*} designType 
 * @param {*} xlsxFile 
 * @param {*} xlsxMemo 
 * @param {*} classSheet 
 * @param {*} methodSheet 
 * @returns 
 */
export function registExcelTemplate(flagNew, designType, xlsxFile, xlsxMemo, classSheet, methodSheet) {
    var fd = new FormData();
    fd.append('type', designType);
    fd.append('file', xlsxFile);
    fd.append('memo', xlsxMemo);
    fd.append('classNumber', classSheet);
    fd.append('methodNumber', methodSheet);

    return net.post(`/v1/design/template/${flagNew}`, fd, {
        timeout: 120000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 표준용어를 반입한다.
 * @param {*} jsonFile 표준용어 데이터
 *            - 다음의 SQL 실행결과를 JOSN으로 저장한다.
 
            SELECT ATRBT_NM AS ATRBT_NM
                ,ATRBT_DESC_CNTNT AS ATRBT_DESC_CNTNT
                -- MySQL
                -- ,IF(ATRBT_TP_CD = 'NU' AND DCML_PNT_LEN = 0, 'long', IF(ATRBT_TP_CD = 'NU' AND DCML_PNT_LEN > 0, 'BigDecimal',  'String')) AS ATRBT_TP_CD
                -- Oracle
                ,CASE WHEN ATRBT_TP_CD = 'NU' AND DCML_PNT_LEN = 0 THEN 'long'
                    WHEN ATRBT_TP_CD = 'NU' AND DCML_PNT_LEN > 0 THEN 'BigDecimal'
                    ELSE 'String'
                END AS ATRBT_TP_CD
                ,ATRBT_LEN AS ATRBT_LEN
                ,DCML_PNT_LEN AS DCML_PNT_LEN
            FROM   CM_STD_ATR_M
            WHERE  LENGTH(ATRBT_DESC_CNTNT) > 0
 *
 * @returns 
 */
export function importStandardAttr(jsonFile) {
    var fd = new FormData();
    fd.append('file', jsonFile);

    return net.post(`/v1/tables/standard`, fd, {
        timeout: 120000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 공통코드 데이터 반입
 * @param {*} cdmFile 
 * @param {*} cddFile 
 * @returns 
 */
export function importCommonCode(cdmFile, cddFile) {
    var fd = new FormData();
    fd.append('fileCdM', cdmFile);
    fd.append('fileCdD', cddFile);

    return net.post(`/v1/tables/code`, fd, {
        timeout: 120000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * ERD 관계 파일 입력
 * @param {*} file 
 * @returns 
 */
export function importERD(file, schema) {
    var fd = new FormData();
    fd.append('file', file);
    fd.append('schema', schema);

    return net.post(`/v1/tables/relations`, fd, {
        timeout: 300000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}


/**
 * UML 메타정보를 입력한다.
 * @param {*} file 
 * @returns 
 */
export function importUML(file) {
    var fd = new FormData();
    fd.append('file', file);

    return net.post(`/v1/uml`, fd, {
        timeout: 120000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 
 * @param {*} modelName 
 * @param {*} uuid 
 * @param {*} file 
 * @returns 
 */
export function saveDocument(modelName, uuid, file) {
    var fd = new FormData();
    fd.append('file', file);

    return net.post(`/v1/docs/${modelName}/${uuid}`, fd, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * L1 리스트를 조회한다.
 * @returns 
 */
export function getL1List() {
    return net.get(`/v1/docs/l1`);
}


/**
 * 모델 다운로드
 * @param {*} modelName 
 * @returns 
 */
export function exportModel(modelName) {
    return net.get(`/v1/export/${modelName}`, {responseType: 'blob'});
}

/**
 * 모델 업로드
 * @param {*} modelName 
 * @param {*} file 
 * @returns 
 */
export function importModel(modelName, file) {
    var fd = new FormData();
    fd.append('file', file);

    return net.post(`/v1/import/${modelName}`, fd, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}
