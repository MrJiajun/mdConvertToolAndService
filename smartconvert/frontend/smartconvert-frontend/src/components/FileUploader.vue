<template>
  <div
    class="upload-zone relative p-12 text-center cursor-pointer"
    :class="{ 'drag-over': isDragging }"
    @dragenter.prevent="isDragging = true"
    @dragleave.prevent="isDragging = false"
    @dragover.prevent
    @drop.prevent="handleDrop"
    @click="triggerFileInput"
  >
    <input
      ref="fileInput"
      type="file"
      class="hidden"
      accept=".docx,.pdf,.txt,.md"
      @change="handleFileChange"
    />
    
    <div v-if="!modelValue" class="space-y-4">
      <div class="flex justify-center">
        <div class="w-20 h-20 bg-primary-100 dark:bg-primary-900 rounded-full flex items-center justify-center">
          <el-icon :size="40" class="text-primary-600 dark:text-primary-400">
            <Upload />
          </el-icon>
        </div>
      </div>
      
      <div>
        <p class="text-lg font-medium text-gray-900 dark:text-white">
          Drop your file here
        </p>
        <p class="text-sm text-gray-500 dark:text-gray-400 mt-2">
          or click to browse
        </p>
      </div>
      
      <div class="flex justify-center gap-4 text-xs text-gray-400">
        <span class="flex items-center gap-1">
          <el-icon><Document /></el-icon>
          DOCX
        </span>
        <span class="flex items-center gap-1">
          <el-icon><Document /></el-icon>
          PDF
        </span>
        <span class="flex items-center gap-1">
          <el-icon><Document /></el-icon>
          TXT
        </span>
        <span class="flex items-center gap-1">
          <el-icon><Document /></el-icon>
          MD
        </span>
      </div>
      
      <p class="text-xs text-gray-400">
        Maximum file size: 10MB
      </p>
    </div>
    
    <div v-else class="space-y-4">
      <div class="flex items-center justify-center gap-4">
        <div class="w-16 h-16 bg-green-100 dark:bg-green-900 rounded-full flex items-center justify-center">
          <el-icon :size="32" class="text-green-600 dark:text-green-400">
            <DocumentChecked />
          </el-icon>
        </div>
        <div class="text-left">
          <p class="font-medium text-gray-900 dark:text-white">
            {{ modelValue.name }}
          </p>
          <p class="text-sm text-gray-500">
            {{ formatFileSize(modelValue.size) }}
          </p>
        </div>
      </div>
      
      <el-button type="danger" plain @click.stop="clearFile">
        <el-icon class="mr-1"><Delete /></el-icon>
        Remove File
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Upload, Document, DocumentChecked, Delete } from '@element-plus/icons-vue'

const props = defineProps<{
  modelValue: File | null
}>()

const emit = defineEmits<{
  'update:modelValue': [file: File | null]
  'upload': [file: File]
}>()

const fileInput = ref<HTMLInputElement | null>(null)
const isDragging = ref(false)

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    processFile(file)
  }
}

const handleDrop = (event: DragEvent) => {
  isDragging.value = false
  const file = event.dataTransfer?.files[0]
  if (file) {
    processFile(file)
  }
}

const processFile = (file: File) => {
  const validExtensions = ['.docx', '.pdf', '.txt', '.md']
  const extension = '.' + file.name.split('.').pop()?.toLowerCase()
  
  if (!validExtensions.includes(extension)) {
    ElMessage.error('Invalid file format. Please upload DOCX, PDF, TXT, or MD files.')
    return
  }
  
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('File size exceeds 10MB limit.')
    return
  }
  
  emit('update:modelValue', file)
  emit('upload', file)
}

const clearFile = () => {
  emit('update:modelValue', null)
  if (fileInput.value) {
    fileInput.value.value = ''
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
