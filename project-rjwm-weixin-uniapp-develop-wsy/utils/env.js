const normalizeBaseUrl = (url) => (url || '').replace(/\/+$/, '')

const defaultBaseUrl = process.env.NODE_ENV === 'development'
	? 'http://localhost:81'
	: 'http://localhost:81'

const defaultWsBaseUrl = process.env.NODE_ENV === 'development'
	? 'ws://localhost:81/ws'
	: 'ws://localhost:81/ws'

export const baseUrl = normalizeBaseUrl(process.env.VUE_APP_API_BASE_URL || defaultBaseUrl)
export const userBaseUrl = baseUrl
export const wsBaseUrl = normalizeBaseUrl(process.env.VUE_APP_WS_BASE_URL || defaultWsBaseUrl)

export const resolveAssetUrl = (path) => {
	if (!path) {
		return ''
	}
	if (/^https?:\/\//i.test(path)) {
		return path
	}
	return `${baseUrl}${path.startsWith('/') ? '' : '/'}${path}`
}
