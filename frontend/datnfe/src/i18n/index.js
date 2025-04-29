// src/i18n/index.js
import { createI18n } from 'vue-i18n'
import en from './en.json'
import vi from './vi.json'

export default createI18n({
    legacy: false,
    globalInjection: true,
    locale: localStorage.getItem('language') || 'vi',
    fallbackLocale: 'vi',
    messages: { en, vi }
})
