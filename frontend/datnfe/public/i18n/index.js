import { createI18n } from 'vue-i18n';
import en from './en.json';
import vi from './vi.json';

const messages = { en, vi };

const i18n = createI18n({
    legacy: false,
    locale: localStorage.getItem('language') || 'vi',
    fallbackLocale: 'vi',
    messages,
});

export default i18n;
