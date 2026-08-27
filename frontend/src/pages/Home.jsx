import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { itemAPI } from '../services/api';

const Home = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [items, setItems] = useState([]);
  const [filteredItems, setFilteredItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const { addToCart } = useCart();

  useEffect(() => {
    loadItems();
  }, []);

  useEffect(() => {
    // Фильтрация товаров по поисковому запросу
    if (searchQuery.trim() === '') {
      setFilteredItems(items);
    } else {
      const filtered = items.filter(item =>
        item.name.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setFilteredItems(filtered);
    }
  }, [items, searchQuery]);

  // Синхронизация с localStorage для поиска
  useEffect(() => {
    const handleStorageChange = () => {
      const storedQuery = localStorage.getItem('searchQuery') || '';
      setSearchQuery(storedQuery);
    };

    // Слушаем изменения в localStorage
    window.addEventListener('storage', handleStorageChange);

    // Проверяем начальное значение
    handleStorageChange();

    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, []);

  const loadItems = async () => {
    try {
      setLoading(true);
      const response = await itemAPI.getAllItems();
      setItems(response.data);
      setError(null);
    } catch (err) {
      console.error('Error loading items:', err);
      setError('Не удалось загрузить товары');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (item) => {
    addToCart({
      id: item.id,
      name: item.name,
      price: item.price,
      imageUrl: item.imageUrl
    });
  };

  const isPopularItem = (item) => {
    // Показываем "ХИТ" для товаров с высоким рейтингом или определенных позиций
    return item.id <= 3 || item.name.toLowerCase().includes('бургер') || item.price > 400;
  };

  return (
    <main className="max-w-screen-xl mx-auto px-4">
      {/* Hero Section */}
      <section className="mb-stack-lg">
        <div className="text-center mb-stack-lg">
          <h1 className="font-headline-xl text-on-surface mb-4">🍔 Burger House</h1>
          <p className="font-body-lg text-secondary max-w-2xl mx-auto">
            Лучшие бургеры в городе с доставкой за 30 минут. Мы используем только свежие ингредиенты
            и готовим каждый бургер с любовью специально для вас.
          </p>

          {!isAuthenticated && (
            <div className="flex gap-3 justify-center mt-6">
              <button
                onClick={() => navigate('/login')}
                className="px-8 py-3 bg-primary-container text-on-primary-fixed rounded-full font-label-sm hover:shadow-lg active:scale-95 transition-all"
              >
                Войти
              </button>
              <button
                onClick={() => navigate('/register')}
                className="px-8 py-3 bg-surface-container-high text-on-surface rounded-full font-label-sm hover:shadow-lg active:scale-95 transition-all"
              >
                Зарегистрироваться
              </button>
            </div>
          )}
        </div>

        <div className="relative h-48 md:h-64 rounded-3xl overflow-hidden bg-surface-container-highest group">
          <img
            className="w-full h-full object-cover"
            alt="Специальное предложение"
            src="https://lh3.googleusercontent.com/aida-public/AB6AXuDzgaVpRBHG213O968G7c-CoLVJYZLAo88mfznoiUbHDAfbM5wd-mAwjEBYdtNmc8J-vcBXT2KFmNEq4mBfBFx0j9A8IXPqJIh8slW2mYZRsZxlAl0_mCQ5SzKCH_uU9jom4LOuf1RNXLB_6fc8RH9FPuo17YVKInWmEbWFSlg53JPpaPawCiPMpjbxl8lGFfdbu4B3VGsDuMFRxXIKvlBZbOw8ORGZ4toDql3IYhmY3DZyKf3GmSNzZVh5p4PxO4rq-2LEsLSZlaE"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-black/60 to-transparent flex flex-col justify-center px-8">
            <h2 className="text-white font-headline-xl mb-2">Вкусные бургеры</h2>
            <p className="text-white/80 font-body-lg max-w-xs">Самые сочные бургеры в городе с доставкой за 30 минут</p>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="mb-stack-lg">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-surface-container-lowest p-6 rounded-2xl text-center shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="w-12 h-12 bg-primary-container rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="material-symbols-outlined text-on-primary-fixed">bolt</span>
            </div>
            <h3 className="font-headline-md text-on-surface mb-2">Быстрая доставка</h3>
            <p className="text-secondary text-sm">Среднее время доставки 25-35 минут</p>
          </div>
          <div className="bg-surface-container-lowest p-6 rounded-2xl text-center shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="w-12 h-12 bg-primary-container rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="material-symbols-outlined text-on-primary-fixed">restaurant</span>
            </div>
            <h3 className="font-headline-md text-on-surface mb-2">Свежие ингредиенты</h3>
            <p className="text-secondary text-sm">Только качественные продукты от проверенных поставщиков</p>
          </div>
          <div className="bg-surface-container-lowest p-6 rounded-2xl text-center shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="w-12 h-12 bg-primary-container rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="material-symbols-outlined text-on-primary-fixed">local_shipping</span>
            </div>
            <h3 className="font-headline-md text-on-surface mb-2">Бесплатная доставка</h3>
            <p className="text-secondary text-sm">Вне зависимости от суммы заказа доставка бесплатно</p>
          </div>
        </div>
      </section>

      {/* Product Grid */}
      <section className="mb-stack-lg pb-20">
        <div className="flex items-center justify-between mb-stack-md">
          <h3 className="font-headline-lg text-on-surface">Наше меню</h3>
          <div className="flex items-center gap-2 text-primary">
            <span className="material-symbols-outlined text-sm">restaurant_menu</span>
            <span className="font-label-sm">Все блюда</span>
          </div>
        </div>

        {loading ? (
          <div className="text-center py-16">
            <div className="inline-block animate-spin">
              <span className="material-symbols-outlined text-6xl text-primary-container">hourglass_top</span>
            </div>
            <p className="mt-6 text-secondary font-body-lg">Загружаем наше вкусное меню...</p>
          </div>
        ) : error ? (
          <div className="text-center py-16">
            <div className="w-16 h-16 bg-error-container rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="material-symbols-outlined text-4xl text-error">error</span>
            </div>
            <h3 className="font-headline-md text-on-surface mb-2">Не удалось загрузить меню</h3>
            <p className="text-error mb-6">{error}</p>
            <button
              onClick={loadItems}
              className="px-8 py-3 bg-primary-container text-on-primary-fixed rounded-full font-label-sm hover:shadow-lg transition-shadow"
            >
              Попробовать снова
            </button>
          </div>
        ) : filteredItems.length === 0 && searchQuery ? (
          <div className="text-center py-16">
            <div className="w-16 h-16 bg-surface-container rounded-full flex items-center justify-center mx-auto mb-4">
              <span className="material-symbols-outlined text-4xl text-on-surface-variant">search_off</span>
            </div>
            <h3 className="font-headline-md text-on-surface mb-2">Ничего не найдено</h3>
            <p className="text-secondary mb-6">Попробуйте изменить запрос или <button onClick={() => {
              setSearchQuery('');
              localStorage.setItem('searchQuery', '');
              window.dispatchEvent(new Event('storage'));
            }} className="text-primary hover:underline">очистить поиск</button></p>
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
            {filteredItems.map((item) => (
              <div key={item.id} className="bg-surface-container-lowest rounded-[24px] overflow-hidden shadow-[0px_4px_20px_rgba(0,0,0,0.04)] group hover:shadow-[0px_8px_30px_rgba(0,0,0,0.08)] transition-all duration-300 flex flex-col">
                <div className="relative aspect-square md:aspect-[4/3] overflow-hidden bg-surface-container">
                  {item.imageUrl ? (
                    <img
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                      alt={item.name}
                      src={item.imageUrl}
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center bg-surface-container-low">
                      <span className="material-symbols-outlined text-4xl text-on-surface-variant">image_not_supported</span>
                    </div>
                  )}
                  <div className="absolute top-2 left-2 bg-white/90 backdrop-blur-md px-3 py-1 rounded-full text-[10px] font-bold flex items-center gap-1 shadow-sm">
                    <span className="material-symbols-outlined text-[12px] text-yellow-500" style={{fontVariationSettings: "'FILL' 1"}}>star</span>
                    4.8
                  </div>
                  {isPopularItem(item) && (
                    <div className="absolute top-2 right-2 bg-primary-container/90 backdrop-blur-md px-2 py-1 rounded-full text-[10px] font-bold text-on-primary-fixed">
                      ХИТ
                    </div>
                  )}
                </div>
                <div className="p-4 flex-1 flex flex-col justify-between">
                  <div>
                    <h4 className="font-headline-md text-on-surface line-clamp-1 mb-1">{item.name}</h4>
                    <p className="text-on-surface-variant text-sm flex items-center gap-1">
                      <span className="material-symbols-outlined text-xs">schedule</span>
                      25-35 мин
                    </p>
                  </div>
                  <div className="flex items-center justify-between mt-4">
                    <div>
                      <span className="font-price-tag text-on-surface text-lg">{item.price} ₽</span>
                      <p className="text-secondary text-xs">Бесплатная доставка</p>
                    </div>
                    <button
                      onClick={() => handleAddToCart(item)}
                      className="w-12 h-12 bg-primary-container text-on-primary-fixed rounded-full flex items-center justify-center active:scale-90 transition-all duration-200 shadow-md hover:shadow-lg hover:bg-primary-container/90"
                    >
                      <span className="material-symbols-outlined font-bold">add</span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Footer Info */}
      <section className="pb-20">
        <div className="bg-surface-container-lowest rounded-3xl p-8 text-center shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          <h3 className="font-headline-lg text-on-surface mb-4">🍔 Почему выбирают Burger House?</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl mx-auto">
            <div className="text-left">
              <h4 className="font-headline-md text-on-surface mb-2">Качество</h4>
              <p className="text-secondary">Мы используем только свежие ингредиенты и готовим каждый бургер индивидуально.</p>
            </div>
            <div className="text-left">
              <h4 className="font-headline-md text-on-surface mb-2">Скорость</h4>
              <p className="text-secondary">Быстрая доставка в течение 25-35 минут по всему городу.</p>
            </div>
            <div className="text-left">
              <h4 className="font-headline-md text-on-surface mb-2">Цена</h4>
              <p className="text-secondary">Доступные цены и бесплатная доставка.</p>
            </div>
            <div className="text-left">
              <h4 className="font-headline-md text-on-surface mb-2">Сервис</h4>
              <p className="text-secondary">Дружелюбная служба поддержки и удобное мобильное приложение.</p>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
};

export default Home;

