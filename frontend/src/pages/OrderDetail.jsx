import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderAPI } from '../services/api';

const OrderDetail = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CARD');
  const [paying, setPaying] = useState(false);
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        setLoading(true);
        const response = await orderAPI.getOrder(id);
        setOrder(response.data);
      } catch (err) {
        setError('Ошибка загрузки заказа');
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [id]);

  const handlePayment = async () => {
    setPaying(true);
    try {
      const response = await orderAPI.payOrder(id, paymentMethod);
      setOrder(response.data);
      setPaymentSuccess(true);
      setTimeout(() => {
        navigate('/orders');
      }, 2000);
    } catch (err) {
      setError('Ошибка при обработке платежа');
    } finally {
      setPaying(false);
    }
  };

  const getStatusInfo = (status) => {
    const statusMap = {
      PENDING_PAYMENT: { color: 'bg-yellow-100 text-yellow-800', icon: 'pending', text: 'Ожидание оплаты' },
      PAID: { color: 'bg-blue-100 text-blue-800', icon: 'payment', text: 'Оплачено' },
      DELIVERY_ASSIGNED: { color: 'bg-purple-100 text-purple-800', icon: 'local_shipping', text: 'В пути' },
      DELIVERED: { color: 'bg-green-100 text-green-800', icon: 'check_circle', text: 'Доставлено' },
      PAYMENT_FAILED: { color: 'bg-red-100 text-red-800', icon: 'error', text: 'Ошибка платежа' },
    };
    return statusMap[status] || { color: 'bg-gray-100 text-gray-800', icon: 'help', text: status };
  };

  if (loading) {
    return (
      <main className="max-w-screen-xl mx-auto px-4 py-10 text-center">
        <div className="inline-block animate-spin">
          <span className="material-symbols-outlined text-4xl">hourglass_top</span>
        </div>
        <p className="mt-4 text-secondary">Загружаем детали заказа...</p>
      </main>
    );
  }

  if (error || !order) {
    return (
      <main className="max-w-screen-xl mx-auto px-4 py-stack-lg">
        <div className="p-4 bg-error-container text-error rounded-xl font-body-md mb-4">
          {error || 'Заказ не найден'}
        </div>
        <button
          onClick={() => navigate('/orders')}
          className="bg-primary-container text-on-primary-fixed px-6 py-2 rounded-full font-label-sm"
        >
          К заказам
        </button>
      </main>
    );
  }

  const statusInfo = getStatusInfo(order.orderStatus);

  if (paymentSuccess) {
    return (
      <main className="max-w-screen-xl mx-auto px-4 py-16 text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
          <span className="material-symbols-outlined text-4xl text-green-600" style={{fontVariationSettings: "'FILL' 1"}}>check_circle</span>
        </div>
        <h1 className="font-headline-xl text-on-surface mt-4">Платеж успешен!</h1>
        <p className="font-body-lg text-secondary mt-2">Переходим к вашим заказам...</p>
      </main>
    );
  }

  return (
    <main className="max-w-screen-xl mx-auto px-4 py-stack-lg pb-32">
      <div className="mb-stack-lg">
        <h1 className="font-headline-xl text-on-surface">Заказ #{order.id}</h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Order Details Section */}
        <div className="lg:col-span-8 space-y-4">
          {/* Status Card */}
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="flex items-start justify-between">
              <div>
                <h3 className="font-headline-md text-on-surface mb-3">Информация о доставке</h3>
                <div className="space-y-3">
                  <p className="font-body-md">
                    <span className="text-secondary">Адрес:</span><br/>
                    <span className="text-on-surface font-semibold">{order.street}, дом {order.houseNumber}</span>
                  </p>
                  {order.courierName && (
                    <p className="font-body-md">
                      <span className="text-secondary">Курьер:</span><br/>
                      <span className="text-on-surface font-semibold">{order.courierName}</span>
                    </p>
                  )}
                  {order.etaMinutes !== undefined && order.etaMinutes !== null && (
                    <p className="font-body-md">
                      <span className="text-secondary">Время доставки:</span><br/>
                      <span className="text-on-surface font-semibold">~{order.etaMinutes} минут</span>
                    </p>
                  )}
                </div>
              </div>
              <span className={`inline-flex items-center gap-1 px-4 py-2 rounded-full text-label-sm font-semibold ${statusInfo.color}`}>
                <span className="material-symbols-outlined text-base">{statusInfo.icon}</span>
                {statusInfo.text}
              </span>
            </div>
          </div>

          {/* Items Card */}
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <h3 className="font-headline-md text-on-surface mb-4">Товары в заказе</h3>
            <div className="space-y-3">
              {order.items && order.items.length > 0 ? (
                order.items.map((item, idx) => (
                  <div key={idx} className="flex items-center justify-between pb-3 border-b border-surface-container-high last:border-b-0">
                    <div>
                      <p className="font-body-lg text-on-surface">{item.name}</p>
                      <p className="font-body-sm text-secondary">Кол-во: {item.quantity}</p>
                    </div>
                    <p className="font-price-tag text-on-surface">{(item.priceAtPurchase * item.quantity).toFixed(0)} ₽</p>
                  </div>
                ))
              ) : (
                <p className="text-center text-secondary py-6">Нет товаров в заказе</p>
              )}
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="lg:col-span-4 space-y-6">
          {/* Order Summary */}
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <h3 className="font-headline-md text-on-surface mb-4">Итого</h3>
            <div className="space-y-3">
              <div className="flex justify-between font-body-md">
                <span className="text-secondary">Сумма товаров</span>
                <span className="text-on-surface">{order.totalAmount} ₽</span>
              </div>
              <div className="flex justify-between font-body-md">
                <span className="text-secondary">Доставка</span>
                <span className="text-on-surface">Бесплатно</span>
              </div>
              <div className="pt-3 border-t border-surface-container-high flex justify-between">
                <span className="font-headline-md text-on-surface">Всего</span>
                <span className="font-headline-md text-on-surface">{order.totalAmount} ₽</span>
              </div>
            </div>
          </div>

          {/* Payment Section */}
          {order.orderStatus === 'PENDING_PAYMENT' && (
            <div className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
              <h3 className="font-headline-md text-on-surface mb-4">Оплата</h3>

              {error && (
                <div className="mb-4 p-3 bg-error-container text-error rounded-lg text-body-sm">
                  {error}
                </div>
              )}

              <div className="space-y-3">
                <label className="font-label-sm text-on-surface-variant">Способ оплаты</label>
                <select
                  value={paymentMethod}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-full bg-surface-container-low border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-3 px-4 font-body-md transition-all outline-none"
                >
                  <option value="CARD">Кредитная карта</option>
                  <option value="QR">QR код</option>
                  <option value="YANDEX_SPLIT">Яндекс.Сплит</option>
                </select>
              </div>

              <button
                onClick={handlePayment}
                disabled={paying}
                className="w-full mt-6 bg-primary-container text-on-primary-fixed py-4 rounded-full font-headline-md shadow-[0px_4px_20px_rgba(255,210,0,0.3)] hover:shadow-[0px_8px_30px_rgba(255,210,0,0.4)] active:scale-95 transition-all duration-200 disabled:opacity-60"
              >
                {paying ? 'Обработка...' : `Оплатить ${order.totalAmount} ₽`}
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Back Button */}
      <button
        onClick={() => navigate('/orders')}
        className="mt-8 text-primary font-label-sm flex items-center gap-2 hover:text-primary/80 transition-colors"
      >
        <span className="material-symbols-outlined">chevron_left</span>
        К заказам
      </button>
    </main>
  );
};

export default OrderDetail;

