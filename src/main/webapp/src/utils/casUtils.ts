/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* eslint-disable ts/no-use-before-define */
/* eslint-disable ts/ban-ts-comment */
import type { Identity } from '@/types'
import { useConfigurationStore } from '@/stores'
import { storeToRefs } from 'pinia'

const { VITE_API_URI } = import.meta.env

// Fonction permettant d'exécutant une requête de type JSONP
function jsonp(uri: string, callbackName: string, timeout: number): Promise<Identity> {
  let url = VITE_API_URI.endsWith('/') ? VITE_API_URI.slice(0, -1) : VITE_API_URI
  url += uri

  return new Promise((resolve, reject) => {
    callbackName = typeof callbackName === 'string'
      ? callbackName
      : `jsonp_${(Math.floor(Math.random() * 100000) * Date.now()).toString(16)}`
    timeout = typeof timeout === 'number' ? timeout : 5000

    let timeoutTimer: NodeJS.Timeout | null = null
    if (timeout > -1) {
      timeoutTimer = setTimeout(() => {
        console.warn('Timeout JSONP')
        removeErrorListener()
        removeScript()
        reject(new Error(JSON.stringify({
          statusText: 'Request Timeout',
          status: 408,
        })))
      }, timeout)
    }

    const onError = (err: Event) => {
      console.error('Error JSONP', err)
      if (timeoutTimer) {
        clearTimeout(timeoutTimer)
      }
      removeErrorListener()
      removeScript()
      reject(new Error(JSON.stringify({
        status: 400,
        statusText: 'Bad Request',
      })))
    }

    // @ts-ignore
    window[callbackName] = (json: Identity | PromiseLike<Identity>) => {
      if (timeoutTimer)
        clearTimeout(timeoutTimer)
      removeErrorListener()
      removeScript()
      resolve(json)
    }

    const script = document.createElement('script')
    script.type = 'text/javascript'

    const removeErrorListener = () => {
      script.removeEventListener('error', onError)
    }

    const removeScript = () => {
      document.body.removeChild(script)
      // @ts-ignore
      delete window[callbackName]
    }

    // Add error listener.
    script.addEventListener('error', onError)

    // Append to head element.
    script.src = `${url + (/\?/.test(url) ? '&' : '?')}callback=${callbackName}`
    script.async = true
    document.body.appendChild(script)
  })
}

interface RelogState {
  listener?: (evt: MessageEvent) => void
  window?: Window | null
}

// Objet en charge de la redirection vers le serveur CAS
const relogState: RelogState = {}

// Méthode en charge du processus de connexion
// Une fois connecté, l'utilisateur est redirigé
async function login(): Promise<void> {
  const configurationStore = useConfigurationStore()
  const { identity } = storeToRefs(configurationStore)

  try {
    const response = await jsonp('/app/login', 'JSON_CALLBACK', 1000)
    identity.value = response
  }
  // eslint-disable-next-line unused-imports/no-unused-vars
  catch (e) {
    identity.value = undefined
    relog()
  }
}

// Méthode effectuant une redirection sur le serveur CAS,
// un listener est mis en place afin de détecter la réponse
// du serveur CAS
function relog(): void {
  windowOpenCleanup(relogState)
  relogState.listener = onmessage
  window.addEventListener('message', onmessage)

  relogState.window = window.open(`${VITE_API_URI}/app/login?postMessage`)
}

// Méthode de nettoyage de la page de login
function windowOpenCleanup(state: RelogState): void {
  try {
    if (state.listener)
      window.removeEventListener('message', state.listener)
    if (state.window)
      state.window.close()
  }
  catch (e) {
    console.error(e)
  }
}

// Méthode utilisé lors de la réception de la réponse
// du serveur CAS puis redirige l'utilisateur
function onmessage(e: MessageEvent): void {
  if (typeof e.data !== 'string')
    return

  const m = e.data.match(/^loggedUser=(.*)$/)
  if (!m)
    return

  windowOpenCleanup(relogState)
  login()
}

export { login }
