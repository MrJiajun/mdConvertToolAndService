# SmartConvert - Multi-format Document Conversion Tool

A modern web-based document format conversion tool supporting Word, PDF, Text, and Markdown bidirectional conversion.

## Features

- **Multi-format Support**: Convert between DOCX, PDF, TXT, and Markdown formats
- **Drag & Drop Upload**: Easy file upload with visual feedback
- **Live Preview**: Real-time Markdown preview with syntax highlighting
- **Dark Mode**: Toggle between light and dark themes
- **Batch Processing**: Support for multiple file conversions
- **Responsive Design**: Works on desktop and mobile devices

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Java Version**: 17
- **Libraries**:
  - Apache POI (Word processing)
  - Apache PDFBox (PDF processing)
- **Build Tool**: Maven

### Frontend
- **Framework**: Vue 3 with Composition API
- **Build Tool**: Vite
- **UI Library**: Element Plus
- **Styling**: Tailwind CSS
- **State Management**: Pinia
- **HTTP Client**: Axios

## Project Structure

```
smartconvert/
├── backend/
│   └── smartconvert-backend/
│       ├── src/main/java/com/smartconvert/
│       │   ├── controller/     # REST API controllers
│       │   ├── service/        # Business logic services
│       │   ├── dto/            # Data transfer objects
│       │   └── config/         # Configuration classes
│       └── pom.xml
├── frontend/
│   └── smartconvert-frontend/
│       ├── src/
│       │   ├── components/     # Vue components
│       │   ├── views/          # Page views
│       │   ├── stores/         # Pinia stores
│       │   └── router/         # Vue Router config
│       └── package.json
└── README.md
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/convert` | Convert document format |
| GET | `/api/download/{fileId}/{format}` | Download converted file |
| GET | `/api/health` | Health check |

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 20 or higher
- Maven 3.8+

### Backend Setup

```bash
cd smartconvert/backend/smartconvert-backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

```bash
cd smartconvert/frontend/smartconvert-frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:3000`

## Supported Conversions

| From | To | Status |
|------|-----|--------|
| DOCX | MD | ✓ |
| MD | DOCX | ✓ |
| PDF | MD | ✓ |
| MD | PDF | ✓ |
| TXT | MD | ✓ |
| MD | TXT | ✓ |

## Configuration

### Backend (application.yml)

```yaml
server:
  port: 8080

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

app:
  upload:
    dir: ${java.io.tmpdir}/smartconvert/uploads
  cleanup:
    interval: 1800000  # 30 minutes
    max-age: 3600000   # 1 hour
```

### Frontend (vite.config.ts)

The frontend is configured with a proxy to forward API requests to the backend:

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## Security

- File type validation for uploads
- File size limits (10MB max)
- Automatic cleanup of temporary files
- CORS configuration for cross-origin requests

## License

MIT License
