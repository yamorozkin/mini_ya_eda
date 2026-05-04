import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { deliveryAPI } from '../services/api';
import Cookies from 'js-cookie';

const Cart = () => {
  const { cart, removeFromCart, updateQuantity, clearCart, getTotalPrice } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [street, setStreet] = useState('');
  const [houseNumber, setHouseNumber] = useState('');
  const [availableStreets, setAvailableStreets] = useState([]);
  const [loading, setLoading] = useState(false);

  React.useEffect(() => {
    const fetchStreets = async () => {
      try {
        const response = await deliveryAPI.getStreets();
        setAvailableStreets(response.data);
      } catch (err) {
        console.error('Error loading streets:', err);
      }
    };

    fetchStreets();
  }, []);

  const handleCheckout = async () => {
    if (!street || !houseNumber) {
      alert('Пожалуйста, заполните адрес доставки');
      return;
    }

    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setLoading(true);
    try {
      const items = cart.map((item) => ({
        itemId: item.id,
        quantity: item.quantity,
      }));

      const token = Cookies.get('token');
      const response = await fetch('http://localhost:8080/api/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          street,
          houseNumber: parseInt(houseNumber),
          items,
        }),
      });

      if (response.ok) {
        const order = await response.json();
        clearCart();
        navigate(`/orders/${order.id}`);
      } else {
        alert('Ошибка при создании заказа');
      }
    } catch (err) {
      alert('Ошибка при создании заказа');
    } finally {
      setLoading(false);
    }
  };

  if (cart.length === 0) {
    return (
      <main className="max-w-screen-xl mx-auto px-4 py-10 text-center">
        <span className="material-symbols-outlined text-8xl text-on-surface-variant/30">shopping_cart</span>
        <h1 className="font-headline-xl text-on-surface mt-4">Корзина пуста</h1>
        <p className="font-body-lg text-secondary mt-2">Добавьте вкусные товары для начала</p>
        <button
          onClick={() => navigate('/')}
          className="mt-8 bg-primary-container text-on-primary-fixed px-8 py-3 rounded-full font-label-sm active:scale-95 transition-transform"
        >
          Продолжить покупки
        </button>
      </main>
    );
  }

  const subtotal = getTotalPrice();
  const deliveryFee = 0;
  const serviceFee = Math.floor(subtotal * 0.02);
  const total = subtotal + deliveryFee + serviceFee;

  return (
    <main className="max-w-screen-xl mx-auto px-4 pt-stack-lg pb-32">
      <div className="mb-stack-lg">
        <h1 className="font-headline-xl text-headline-xl">Ваша корзина</h1>
        <p className="font-body-md text-body-md text-secondary">Проверьте товары и оформите заказ</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Items Section */}
        <div className="lg:col-span-8 space-y-4">
          {cart.map((item) => (
            <div key={item.id} className="bg-surface-container-lowest p-4 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] flex gap-4 items-center">
              {item.imageUrl ? (
                <img className="w-20 h-20 object-cover rounded-lg" alt={item.name} src={item.imageUrl} />
              ) : (
                <div className="w-20 h-20 bg-surface-container rounded-lg flex items-center justify-center">
                  <span className="material-symbols-outlined text-on-surface-variant text-2xl">image_not_supported</span>
                </div>
              )}
              <div className="flex-grow">
                <h3 className="font-headline-md text-headline-md">{item.name}</h3>
                <p className="font-body-md text-body-md text-secondary">Вкусный товар</p>
                <div className="flex items-center justify-between mt-2">
                  <span className="font-price-tag text-price-tag">{item.price} ₽</span>
                  <div className="flex items-center gap-3 bg-surface-container rounded-full px-3 py-1">
                    <button
                      onClick={() => updateQuantity(item.id, item.quantity - 1)}
                      className="material-symbols-outlined text-body-lg hover:text-primary transition-colors"
                    >
                      remove
                    </button>
                    <span className="font-body-lg text-body-lg w-6 text-center">{item.quantity}</span>
                    <button
                      onClick={() => updateQuantity(item.id, item.quantity + 1)}
                      className="material-symbols-outlined text-body-lg hover:text-primary transition-colors"
                    >
                      add
                    </button>
                    <button
                      onClick={() => removeFromCart(item.id)}
                      className="material-symbols-outlined text-error ml-2 text-sm"
                    >
                      close
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Checkout Details Section */}
        <div className="lg:col-span-4 space-y-6">
          {/* Delivery Address */}
          <section className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="flex items-center justify-between mb-4">
              <h2 className="font-headline-md text-headline-md">Адрес доставки</h2>
            </div>
            <div className="space-y-3">
              <div>
                <label className="font-label-sm text-on-surface-variant px-1">Улица</label>
                <select
                  value={street}
                  onChange={(e) => setStreet(e.target.value)}
                  className="w-full bg-surface-container-low border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-3 px-4 font-body-md transition-all outline-none mt-1"
                >
                  <option value="">Выберите улицу</option>
                  {availableStreets.map((streetName) => (
                    <option key={streetName} value={streetName}>
                      {streetName}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="font-label-sm text-on-surface-variant px-1">Номер дома</label>
                <input
                  type="number"
                  value={houseNumber}
                  onChange={(e) => setHouseNumber(e.target.value)}
                  className="w-full bg-surface-container-low border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-3 px-4 font-body-md transition-all outline-none mt-1"
                  placeholder="Введите номер дома"
                />
              </div>
            </div>
            <div className="mt-4 pt-4 border-t border-neutral-100 flex items-center gap-3">
              <div className="bg-surface-container p-2 rounded-full">
                <span className="material-symbols-outlined text-on-surface">schedule</span>
              </div>
              <p className="font-body-md text-body-md">Доставка в <span className="font-bold">25-35 мин</span></p>
            </div>
          </section>

          {/* Order Summary */}
          <section className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <h2 className="font-headline-md text-headline-md mb-4">Итого</h2>
            <div className="space-y-3">
              <div className="flex justify-between font-body-md text-body-md">
                <span className="text-secondary">Сумма товаров</span>
                <span>{subtotal.toFixed(0)} ₽</span>
              </div>
              <div className="flex justify-between font-body-md text-body-md">
                <span className="text-secondary">Доставка</span>
                <span>{deliveryFee} ₽</span>
              </div>
              <div className="flex justify-between font-body-md text-body-md">
                <span className="text-secondary">Комиссия</span>
                <span>{serviceFee} ₽</span>
              </div>
              <div className="pt-4 border-t border-neutral-100 flex justify-between items-center">
                <span className="font-headline-lg text-headline-lg">Всего</span>
                <span className="font-headline-lg text-headline-lg text-on-surface">{total.toFixed(0)} ₽</span>
              </div>
            </div>
          </section>

          {/* Checkout Button */}
          <button
            onClick={handleCheckout}
            disabled={loading || !street || !houseNumber}
            className="w-full bg-primary-container text-on-primary-fixed py-6 rounded-3xl font-headline-md shadow-[0px_8px_30px_rgba(0,0,0,0.08)] active:scale-95 transition-transform duration-200 disabled:opacity-60"
          >
            {loading ? 'Обработка...' : `Оплатить ${total.toFixed(0)} ₽`}
          </button>
        </div>
      </div>
    </main>
  );
};

export default Cart;

