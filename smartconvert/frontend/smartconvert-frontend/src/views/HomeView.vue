<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <el-icon :size="32" class="text-primary-600">
            <Document />
          </el-icon>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
            SmartConvert
          </h1>
        </div>
        <el-switch
          v-model="isDark"
          active-text="Dark"
          inactive-text="Light"
          :active-icon="Moon"
          :inactive-icon="Sunny"
        />
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Upload Section -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-8 mb-8">
        <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-6">
          Upload Your Document
        </h2>
        
        <FileUploader
          v-model="selectedFile"
          @upload="handleFileUpload"
        />
      </div>

      <!-- Conversion Options -->
      <div v-if="selectedFile" class="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-8 mb-8">
        <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-6">
          Conversion Options
        </h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Source Format
            </label>
            <el-select
              v-model="sourceFormat"
              placeholder="Select source format"
              class="w-full"
              disabled
            >
              <el-option
                v-for="format in supportedFormats"
                :key="format.value"
                :label="format.label"
                :value="format.value"
              />
            </el-select>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Target Format
            </label>
            <el-select
              v-model="targetFormat"
              placeholder="Select target format"
              class="w-full"
            >
              <el-option
                v-for="format in availableTargetFormats"
                :key="format.value"
                :label="format.label"
                :value="format.value"
              />
            </el-select>
          </div>
        </div>

        <div class="mt-6 flex justify-center">
          <el-button
            type="primary"
            size="large"
            :loading="conversionStore.isConverting"
            :disabled="!targetFormat || sourceFormat === targetFormat"
            @click="handleConvert"
          >
            <el-icon class="mr-2"><Switch /></el-icon>
            Convert Document
          </el-button>
        </div>
      </div>

      <!-- Preview Section -->
      <div v-if="markdownContent" class="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-8 mb-8">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-semibold text-gray-900 dark:text-white">
            Preview
          </h2>
          <el-radio-group v-model="previewMode" size="small">
            <el-radio-button label="split">Split</el-radio-button>
            <el-radio-button label="preview">Preview Only</el-radio-button>
          </el-radio-group>
        </div>
        
        <MarkdownPreview
          v-model="markdownContent"
          :mode="previewMode"
        />
      </div>

      <!-- Result Section -->
      <div v-if="conversionStore.conversionResult?.success" class="bg-white dark:bg-gray-800 rounded-xl shadow-lg p-8">
        <div class="text-center">
          <el-icon :size="64" class="text-green-500 mb-4">
            <CircleCheck />
          </el-icon>
          <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">
            Conversion Successful!
          </h2>
          <p class="text-gray-600 dark:text-gray-400 mb-6">
            {{ conversionStore.conversionResult.fileName }}
            ({{ formatFileSize(conversionStore.conversionResult.fileSize || 0) }})
          </p>
          <el-button
            type="success"
            size="large"
            @click="handleDownload"
          >
            <el-icon class="mr-2"><Download /></el-icon>
            Download File
          </el-button>
        </div>
      </div>

      <!-- Error Section -->
      <el-alert
        v-if="conversionStore.error"
        :title="conversionStore.error"
        type="error"
        closable
        @close="conversionStore.clearResult()"
        class="mt-4"
      />
    </main>

    <!-- Footer -->
    <footer class="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 mt-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 text-center text-gray-600 dark:text-gray-400">
        <p>SmartConvert - Multi-format Document Conversion Tool</p>
        <p class="text-sm mt-2">Supports Word, PDF, Text, and Markdown formats</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Document, Switch, CircleCheck, Download, Moon, Sunny } from '@element-plus/icons-vue'
import { useConversionStore } from '@/stores/conversion'
import FileUploader from '@/components/FileUploader.vue'
import MarkdownPreview from '@/components/MarkdownPreview.vue'

const conversionStore = useConversionStore()

const selectedFile = ref<File | null>(null)
const sourceFormat = ref('')
const targetFormat = ref('')
const markdownContent = ref('')
const previewMode = ref('split')
const isDark = ref(false)

const supportedFormats = [
  { value: 'docx', label: 'Word Document (.docx)' },
  { value: 'pdf', label: 'PDF Document (.pdf)' },
  { value: 'txt', label: 'Text File (.txt)' },
  { value: 'md', label: 'Markdown (.md)' },
]

const availableTargetFormats = computed(() => {
  return supportedFormats.filter(f => f.value !== sourceFormat.value)
})

watch(selectedFile, (file) => {
  if (file) {
    const extension = file.name.split('.').pop()?.toLowerCase() || ''
    if (['docx', 'pdf', 'txt', 'md'].includes(extension)) {
      sourceFormat.value = extension
      targetFormat.value = ''
    }
  }
})

watch(isDark, (dark) => {
  if (dark) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
})

const handleFileUpload = async (file: File) => {
  selectedFile.value = file
  
  if (file.name.endsWith('.md') || file.name.endsWith('.txt')) {
    const text = await file.text()
    markdownContent.value = text
  } else {
    markdownContent.value = ''
  }
}

const handleConvert = async () => {
  if (!selectedFile.value || !targetFormat.value) return
  
  try {
    const result = await conversionStore.convertFile(
      selectedFile.value,
      sourceFormat.value,
      targetFormat.value
    )
    
    if (result.success && targetFormat.value === 'md') {
    }
  } catch (error) {
    console.error('Conversion error:', error)
  }
}

const handleDownload = () => {
  if (conversionStore.conversionResult?.downloadUrl) {
    conversionStore.downloadFile(
      conversionStore.conversionResult.downloadUrl,
      conversionStore.conversionResult.fileName || 'converted-file'
    )
  }
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>
