# Inventory Management System - Frontend

Production-grade React frontend for Multi-Tenant Inventory & Order Management System.

## 🚀 Tech Stack

- **React 19** - UI framework
- **Vite** - Build tool & dev server
- **Tailwind CSS** - Utility-first CSS
- **React Router DOM** - Routing
- **TanStack React Query** - Server state management
- **Axios** - HTTP client with interceptors
- **React Hook Form** - Form management
- **React Toastify** - Toast notifications
- **Lucide React** - Icon library

## 📁 Project Structure

```
src/
├── api/                    # API layer
│   ├── axios.js           # Axios instance with interceptors
│   ├── auth.api.js        # Auth endpoints
│   ├── product.api.js     # Product endpoints
│   ├── category.api.js    # Category endpoints
│   └── order.api.js       # Order endpoints
├── components/
│   ├── common/            # Reusable UI components
│   ├── layout/            # Layout components (Sidebar, Navbar)
│   └── auth/              # Auth components (ProtectedRoute)
├── features/              # Feature modules (coming soon)
├── pages/                 # Page components
├── routes/                # Routing configuration
├── store/                 # Global state (AuthContext)
├── hooks/                 # Custom hooks (usePermissions)
├── utils/                 # Utilities (formatters, validators)
├── config.js              # Environment configuration
└── main.jsx               # Entry point
```

## 🛠️ Setup & Installation

### Prerequisites
- Node.js 18+ and npm
- Backend server running on `http://localhost:8080`

### Install Dependencies
```bash
cd frontend
npm install
```

### Environment Variables
Create a `.env` file in the frontend directory:
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_NAME=Inventory Management System
VITE_APP_VERSION=1.0.0
```

### Run Development Server
```bash
npm run dev
```
App will be available at: `http://localhost:5173`

### Build for Production
```bash
npm run build
```

### Preview Production Build
```bash
npm run preview
```

## 🔐 Authentication Flow

1. User enters username/password on login page
2. API call to `/auth/login` returns JWT token + user data
3. Token stored in `localStorage` and attached to all requests via Axios interceptor
4. On 401 response, token is cleared and user redirected to login
5. Protected routes check authentication status before rendering

## 👥 Role-Based Access Control

### Roles
- **SUPER_ADMIN** - Full platform access
- **ADMIN** - Full organization access
- **STAFF** - Limited access (view + create orders)

### Permissions (usePermissions hook)
```javascript
const { 
  canCreateProduct,  // ADMIN, SUPER_ADMIN
  canEditProduct,    // ADMIN, SUPER_ADMIN
  canCreateOrder,    // All authenticated users
  canViewDashboard   // All authenticated users
} = usePermissions();
```

## 📡 API Integration

All API calls use:
- **Axios interceptor** for JWT token injection
- **React Query** for caching, refetching, and mutations
- **Toast notifications** for success/error feedback

### Example: Fetching Products
```javascript
import { useQuery } from '@tanstack/react-query';
import { getProducts } from '../api/product.api';

const { data, isLoading, error } = useQuery({
  queryKey: ['products', { page: 0, size: 20 }],
  queryFn: () => getProducts({ page: 0, size: 20 }),
});
```

## 🎨 UI Components

### Button
```javascript
<Button variant="primary" size="md" loading={isLoading}>
  Save
</Button>
```

### Input
```javascript
<Input 
  label="Product Name"
  name="name"
  value={value}
  onChange={handleChange}
  error={errors.name}
  required
/>
```

### Table
```javascript
<Table
  columns={[
    { header: 'Name', accessor: 'name' },
    { header: 'Price', render: (row) => formatCurrency(row.price) },
  ]}
  data={products}
  loading={isLoading}
/>
```

## 📋 Features Status

- ✅ **Step 1-4**: Complete foundation (setup, auth, layout, routing)
- 🚧 **Step 5**: Categories CRUD (in progress)
- ⏳ **Step 6**: Products CRUD
- ⏳ **Step 7**: Orders module
- ⏳ **Step 8**: Dashboard with stats

## 🔧 Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## 📝 Default Login Credentials

```
Username: admin
Password: password
```

(These are created by the backend DataInitializer)

## 🎯 Next Steps

1. Complete Categories CRUD module
2. Build Products CRUD with search/filters
3. Implement Orders module
4. Add Dashboard stats
5. Polish UI and responsive design

---

**Built with ❤️ using React + Vite + Tailwind CSS**

