import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

const Navigation = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const { cart, clearCart } = useCart();

  const handleLogout = () => {
    clearCart();
    logout();
  };

  return (
    <header className="bg-white/80 dark:bg-neutral-900/80 backdrop-blur-md fixed top-0 w-full z-50 border-b border-neutral-50 dark:border-neutral-800 shadow-[0px_4px_20px_rgba(0,0,0,0.04)] h-16">
      <div className="flex items-center justify-between px-4 h-full max-w-screen-xl mx-auto gap-4">
        <Link to="/" className="text-xl font-black text-neutral-900 dark:text-white shrink-0">
          QuickBite
        </Link>

        <div className="hidden md:flex items-center gap-6 font-sans text-sm font-medium">
          <Link to="/" className="text-neutral-900 dark:text-white font-bold hover:text-primary transition-colors">Главная</Link>

          {isAuthenticated && (
            <>
              <Link to="/orders" className="text-neutral-400 dark:text-neutral-500 hover:text-neutral-900 dark:hover:text-white transition-colors">Мои заказы</Link>
              <Link to="/profile" className="text-neutral-400 dark:text-neutral-500 hover:text-neutral-900 dark:hover:text-white transition-colors">Профиль</Link>
            </>
          )}
        </div>

        <div className="flex-1 max-w-md mx-4">
          <div className="relative flex items-center bg-surface-container-low rounded-full px-4 py-2 hover:bg-surface-container-high transition-colors">
            <span className="material-symbols-outlined text-on-surface-variant mr-2">search</span>
            <input
              className="bg-transparent border-none focus:ring-0 w-full text-sm placeholder:text-on-surface-variant/60 outline-none"
              placeholder="Поиск еды"
              type="text"
            />
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button className="p-2 hover:bg-neutral-50 dark:hover:bg-neutral-800 transition-colors rounded-full active:scale-95 duration-200">
            <span className="material-symbols-outlined text-neutral-900 dark:text-white">location_on</span>
          </button>

          {user?.role === 'ADMIN' && (
            <Link
              to="/admin/create-item"
              className="p-2 hover:bg-neutral-50 dark:hover:bg-neutral-800 transition-colors rounded-full active:scale-95 duration-200 text-warning"
              title="Добавить товар"
            >
              <span className="material-symbols-outlined">add_circle</span>
            </Link>
          )}

          <Link
            to="/cart"
            className="p-2 hover:bg-neutral-50 dark:hover:bg-neutral-800 transition-colors rounded-full active:scale-95 duration-200 relative"
          >
            <span className="material-symbols-outlined text-neutral-900 dark:text-white">shopping_cart</span>
            {cart.length > 0 && (
              <span className="absolute top-1 right-1 bg-primary-container text-[10px] font-bold w-4 h-4 flex items-center justify-center rounded-full text-on-primary-fixed">
                {cart.length}
              </span>
            )}
          </Link>

          {isAuthenticated && (
            <button
              onClick={handleLogout}
              className="p-2 hover:bg-neutral-50 dark:hover:bg-neutral-800 transition-colors rounded-full active:scale-95 duration-200 ml-2"
              title="Выход"
            >
              <span className="material-symbols-outlined text-error">logout</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
};

export default Navigation;
