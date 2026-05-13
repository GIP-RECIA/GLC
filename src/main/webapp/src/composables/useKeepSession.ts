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

import { onMounted, onUnmounted, ref } from 'vue'
import { checkSession } from '@/services/api/index.ts'

const { VITE_KEEP_SESSION_INTERVAL } = import.meta.env

const sessionState = ref<boolean>(true)

export function useKeepSession() {
  let interval: ReturnType<typeof setInterval> | null = null

  const check = async (): Promise<void> => {
    try {
      sessionState.value = await checkSession()
    }
    // eslint-disable-next-line unused-imports/no-unused-vars
    catch (_) {
      console.warn('No session')
      sessionState.value = false
      // eslint-disable-next-line ts/no-use-before-define
      stop()
    }
  }

  const start = (): void => {
    if (interval)
      return

    interval = setInterval(() => {
      if (document.visibilityState === 'visible')
        check()
    }, Number(VITE_KEEP_SESSION_INTERVAL))
  }

  const stop = (): void => {
    if (interval) {
      clearInterval(interval)
      interval = null
    }
  }

  const handleVisibilityChange = (): void => {
    if (document.visibilityState === 'visible') {
      start()
      check()
    }
    else {
      stop()
    }
  }

  onMounted(() => {
    check()
    document.addEventListener('visibilitychange', handleVisibilityChange)
    if (document.visibilityState === 'visible')
      start()
  })

  onUnmounted(() => {
    document.removeEventListener('visibilitychange', handleVisibilityChange)
    stop()
  })

  return {
    start,
    stop,
    sessionState,
  }
}
