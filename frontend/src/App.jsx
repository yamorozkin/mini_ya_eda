import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Navigation from './components/Navigation';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Cart from './pages/Cart';
import Orders from './pages/Orders';
import OrderDetail from './pages/OrderDetail';
import Profile from './pages/Profile';
import CreateItem from './pages/CreateItem';

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div className="text-center py-5">Загрузка...</div>;
  }

  return isAuthenticated ? children : <Navigate to="/login" />;
};

const BottomNav = () => {
  const location = useLocation();
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) return null;

  return (
    <nav className="fixed bottom-0 w-full z-50 rounded-t-3xl bg-white dark:bg-neutral-900 border-t border-neutral-100 dark:border-neutral-800 shadow-[0px_-8px_30px_rgba(0,0,0,0.08)] md:hidden">
      <div className="flex justify-around items-center h-20 px-2">
        <NavItem to="/" icon="home" label="Главная" active={location.pathname === '/'} />
        <NavItem to="/orders" icon="receipt_long" label="Заказы" active={location.pathname === '/orders'} />
        <NavItem to="/cart" icon="shopping_bag" label="Корзина" active={location.pathname === '/cart'} />
        <NavItem to="/profile" icon="person" label="Профиль" active={location.pathname === '/profile'} />
      </div>
    </nav>
  );
};

const NavItem = ({ to, icon, label, active }) => {
  return (
    <a
      href={to}
      className={`flex flex-col items-center justify-center px-4 py-2 active:scale-90 transition-transform duration-150 ${
        active
          ? 'text-neutral-900 dark:text-white bg-yellow-400 rounded-2xl'
          : 'text-neutral-400 dark:text-neutral-500 hover:text-yellow-500'
      }`}
    >
      <span className="material-symbols-outlined">{icon}</span>
      <span className="font-sans text-[11px] font-semibold mt-1">{label}</span>
    </a>
  );
};

function AppContent() {
  return (
    <Router>
      <Navigation />
      <main className="pt-20 pb-24 md:pb-8 min-h-screen">
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/cart" element={<Cart />} />

          {/* Protected routes */}
          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <Orders />
              </ProtectedRoute>
            }
          />
          <Route
            path="/orders/:id"
            element={
              <ProtectedRoute>
                <OrderDetail />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/create-item"
            element={
              <ProtectedRoute>
                <CreateItem />
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
      <BottomNav />
    </Router>
  );
}

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <AppContent />
      </CartProvider>
    </AuthProvider>
  );
}

export default App;

