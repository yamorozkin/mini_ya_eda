import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

const Navigation = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const { cart, clearCart } = useCart();
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    // Загружаем начальное значение из localStorage
    const storedQuery = localStorage.getItem('searchQuery') || '';
    setSearchQuery(storedQuery);
  }, []);

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchQuery(value);
    localStorage.setItem('searchQuery', value);

    // Создаем событие для синхронизации между вкладками
    window.dispatchEvent(new Event('storage'));
  };

  const handleLogout = () => {
    clearCart();
    logout();
  };

  return (
    <header className="bg-white/80 dark:bg-neutral-900/80 backdrop-blur-md fixed top-0 w-full z-50 border-b border-neutral-50 dark:border-neutral-800 shadow-[0px_4px_20px_rgba(0,0,0,0.04)] h-16">
      <div className="flex items-center justify-between px-4 h-full max-w-screen-xl mx-auto gap-4">
        <Link to="/" className="text-xl font-black text-neutral-900 dark:text-white shrink-0">
          Burger House
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
              value={searchQuery}
              onChange={handleSearchChange}
              className="bg-transparent border-none focus:ring-0 w-full text-sm placeholder:text-on-surface-variant/60 outline-none"
              placeholder="Поиск блюд..."
              type="text"
            />
            {searchQuery && (
              <button
                onClick={() => {
                  setSearchQuery('');
                  localStorage.setItem('searchQuery', '');
                  window.dispatchEvent(new Event('storage'));
                }}
                className="ml-2 text-on-surface-variant hover:text-on-surface transition-colors"
              >
                <span className="material-symbols-outlined text-sm">close</span>
              </button>
            )}
          </div>
        </div>

        <div className="flex items-center gap-2">
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
