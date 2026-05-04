import React, { useState, useEffect } from 'react';
import { useCart } from '../context/CartContext';
import { itemAPI } from '../services/api';

const Home = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { addToCart } = useCart();

  useEffect(() => {
    loadItems();
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

  return (
    <main className="max-w-screen-xl mx-auto px-4">
      {/* Promotional Hero */}
      <section className="mb-stack-lg">
        <div className="relative h-48 md:h-64 rounded-3xl overflow-hidden bg-surface-container-highest group">
          <img
            className="w-full h-full object-cover"
            alt="Специальное предложение"
            src="https://lh3.googleusercontent.com/aida-public/AB6AXuDzgaVpRBHG213O968G7c-CoLVJYZLAo88mfznoiUbHDAfbM5wd-mAwjEBYdtNmc8J-vcBXT2KFmNEq4mBfBFx0j9A8IXPqJIh8slW2mYZRsZxlAl0_mCQ5SzKCH_uU9jom4LOuf1RNXLB_6fc8RH9FPuo17YVKInWmEbWFSlg53JPpaPawCiPMpjbxl8lGFfdbu4B3VGsDuMFRxXIKvlBZbOw8ORGZ4toDql3IYhmY3DZyKf3GmSNzZVh5p4PxO4rq-2LEsLSZlaE"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-black/60 to-transparent flex flex-col justify-center px-8">
            <h2 className="text-white font-headline-xl mb-2">Бесплатная доставка</h2>
            <p className="text-white/80 font-body-lg max-w-xs">При первых 3 заказах из местных ресторанов. Ограниченное предложение.</p>
            <button className="mt-4 bg-primary-container text-on-primary-fixed w-fit px-8 py-3 rounded-full font-label-sm active:scale-95 transition-transform">Получить предложение</button>
          </div>
        </div>
      </section>

      {/* Product Grid */}
      <section className="mb-stack-lg pb-20">
        <div className="flex items-center justify-between mb-stack-md">
          <h3 className="font-headline-lg text-on-surface">Популярные товары</h3>
        </div>

        {loading ? (
          <div className="text-center py-10">
            <div className="inline-block animate-spin">
              <span className="material-symbols-outlined text-4xl">hourglass_top</span>
            </div>
            <p className="mt-4 text-secondary">Загружаем меню...</p>
          </div>
        ) : error ? (
          <div className="text-center py-10">
            <p className="text-error">{error}</p>
            <button
              onClick={loadItems}
              className="mt-4 px-6 py-2 bg-primary-container text-on-primary-fixed rounded-full font-label-sm"
            >
              Попробовать снова
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
            {items.map((item) => (
              <div key={item.id} className="bg-surface-container-lowest rounded-[24px] overflow-hidden shadow-[0px_4px_20px_rgba(0,0,0,0.04)] group flex flex-col">
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
                  <div className="absolute top-2 left-2 bg-white/90 backdrop-blur-md px-3 py-1 rounded-full text-[10px] font-bold flex items-center gap-1">
                    <span className="material-symbols-outlined text-[12px] text-yellow-500" style={{fontVariationSettings: "'FILL' 1"}}>star</span>
                    4.8
                  </div>
                </div>
                <div className="p-4 flex-1 flex flex-col justify-between">
                  <div>
                    <h4 className="font-headline-md text-on-surface line-clamp-1">{item.name}</h4>
                    <p className="text-on-surface-variant text-sm mt-1">20-30 мин • 450г</p>
                  </div>
                  <div className="flex items-center justify-between mt-4">
                    <span className="font-price-tag text-on-surface">{item.price} ₽</span>
                    <button
                      onClick={() => handleAddToCart(item)}
                      className="w-10 h-10 bg-primary-container text-on-primary-fixed rounded-full flex items-center justify-center active:scale-90 transition-transform shadow-md hover:shadow-lg"
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
    </main>
  );
};

export default Home;

