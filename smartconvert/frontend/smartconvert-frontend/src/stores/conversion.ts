import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export interface ConversionResult {
  success: boolean
  message: string
  downloadUrl?: string
  fileName?: string
  fileSize?: number
}

export const useConversionStore = defineStore('conversion', () => {
  const isConverting = ref(false)
  const conversionResult = ref<ConversionResult | null>(null)
  const error = ref<string | null>(null)

  const convertFile = async (
    file: File,
    sourceFormat: string,
    targetFormat: string
  ): Promise<ConversionResult> => {
    isConverting.value = true
    error.value = null
    conversionResult.value = null

    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('sourceFormat', sourceFormat)
      formData.append('targetFormat', targetFormat)

      const response = await axios.post('/api/convert', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })

      conversionResult.value = response.data
      return response.data
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Conversion failed'
      error.value = errorMessage
      throw new Error(errorMessage)
    } finally {
      isConverting.value = false
    }
  }

  const downloadFile = (downloadUrl: string, fileName: string) => {
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  const clearResult = () => {
    conversionResult.value = null
    error.value = null
  }

  return {
    isConverting,
    conversionResult,
    error,
    convertFile,
    downloadFile,
    clearResult,
  }
})
