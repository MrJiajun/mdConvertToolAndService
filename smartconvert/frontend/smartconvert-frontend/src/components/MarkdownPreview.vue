<template>
  <div class="grid gap-6" :class="gridClass">
    <!-- Editor -->
    <div v-if="mode === 'split'" class="preview-panel overflow-hidden">
      <div class="bg-gray-100 dark:bg-gray-700 px-4 py-2 border-b border-gray-200 dark:border-gray-600">
        <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Markdown Source</span>
      </div>
      <textarea
        :value="modelValue"
        @input="updateContent"
        class="w-full h-96 p-4 resize-none focus:outline-none bg-white dark:bg-gray-800 text-gray-900 dark:text-white font-mono text-sm"
        placeholder="Enter markdown content..."
      />
    </div>

    <!-- Preview -->
    <div class="preview-panel overflow-hidden">
      <div class="bg-gray-100 dark:bg-gray-700 px-4 py-2 border-b border-gray-200 dark:border-gray-600 flex items-center justify-between">
        <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Preview</span>
        <div class="flex gap-2">
          <el-button
            v-if="mode === 'preview'"
            size="small"
            @click="showEditor = !showEditor"
          >
            {{ showEditor ? 'Hide Editor' : 'Show Editor' }}
          </el-button>
          <el-button
            size="small"
            @click="copyContent"
          >
            <el-icon class="mr-1"><CopyDocument /></el-icon>
            Copy
          </el-button>
        </div>
      </div>
      
      <!-- Editor overlay for preview mode -->
      <div v-if="showEditor && mode === 'preview'" class="mb-4">
        <textarea
          :value="modelValue"
          @input="updateContent"
          class="w-full h-48 p-4 resize-none focus:outline-none bg-white dark:bg-gray-800 text-gray-900 dark:text-white font-mono text-sm border border-gray-200 dark:border-gray-700 rounded-lg"
          placeholder="Enter markdown content..."
        />
      </div>
      
      <div 
        class="markdown-content p-6 h-96 overflow-y-auto bg-white dark:bg-gray-800"
        v-html="renderedMarkdown"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { marked } from 'marked'
import { CopyDocument } from '@element-plus/icons-vue'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const props = defineProps<{
  modelValue: string
  mode: 'split' | 'preview'
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const showEditor = ref(false)

const gridClass = computed(() => {
  return props.mode === 'split' ? 'grid-cols-1 lg:grid-cols-2' : 'grid-cols-1'
})

marked.setOptions({
  highlight: (code, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true,
  gfm: true,
})

const renderedMarkdown = computed(() => {
  return marked.parse(props.modelValue || '')
})

const updateContent = (event: Event) => {
  const target = event.target as HTMLTextAreaElement
  emit('update:modelValue', target.value)
}

const copyContent = () => {
  navigator.clipboard.writeText(props.modelValue)
  ElMessage.success('Content copied to clipboard')
}
</script>
