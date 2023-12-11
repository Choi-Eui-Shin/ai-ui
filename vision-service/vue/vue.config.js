module.exports = {
    pluginOptions: {
        i18n: {
            locale: 'ko',
            fallbackLocale: 'ko',
            localeDir: 'locales',
            enableInSFC: true
        }
    },

    transpileDependencies: [
        'vuetify'
    ],

    // 개발 서버 설정
    devServer: {
        // 프록시 설정
        proxy: {
            "/v1/": {
                // 프록시 요청을 보낼 서버의 주소
                target: "http://localhost:8080"
            }
        }
    },
}
