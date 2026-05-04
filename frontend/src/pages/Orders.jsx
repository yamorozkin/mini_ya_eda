import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    const fetchOrders = async () => {
      try {
        setLoading(true);
        const response = await orderAPI.getAllOrders();
        setOrders(response.data);
      } catch (err) {
        setError('Ошибка загрузки заказов');
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [isAuthenticated, navigate]);

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
        <p className="mt-4 text-secondary">Загружаем ваши заказы...</p>
      </main>
    );
  }

  return (
    <main className="max-w-screen-xl mx-auto px-4 py-stack-lg pb-32">
      <div className="mb-stack-lg">
        <h1 className="font-headline-xl text-on-surface">Мои заказы</h1>
        <p className="font-body-lg text-secondary">Отслеживайте и управляйте своими заказами</p>
      </div>

      {error && (
        <div className="mb-stack-lg p-4 bg-error-container text-error rounded-xl font-body-md">
          {error}
        </div>
      )}

      {orders.length === 0 ? (
        <div className="text-center py-16">
          <span className="material-symbols-outlined text-8xl text-on-surface-variant/30">receipt_long</span>
          <h2 className="font-headline-lg text-on-surface mt-4">Заказов нет</h2>
          <p className="font-body-lg text-secondary mt-2">Начните заказывать вкусную еду прямо сейчас!</p>
          <button
            onClick={() => navigate('/')}
            className="mt-8 bg-primary-container text-on-primary-fixed px-8 py-3 rounded-full font-label-sm active:scale-95 transition-transform"
          >
            Начать покупки
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => {
            const statusInfo = getStatusInfo(order.orderStatus);
            return (
              <div
                key={order.id}
                onClick={() => navigate(`/orders/${order.id}`)}
                className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] hover:shadow-[0px_8px_30px_rgba(0,0,0,0.08)] transition-shadow cursor-pointer"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-grow">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="font-headline-md text-on-surface">Заказ #{order.id}</h3>
                      <span className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-label-sm font-semibold ${statusInfo.color}`}>
                        <span className="material-symbols-outlined text-sm">{statusInfo.icon}</span>
                        {statusInfo.text}
                      </span>
                    </div>
                    <p className="font-body-md text-secondary mb-2">
                      📍 {order.street}, дом {order.houseNumber}
                    </p>
                    <p className="font-body-sm text-secondary">
                      {order.items?.length || 0} товаров • Всего: <span className="font-bold text-on-surface">{order.totalAmount} ₽</span>
                    </p>
                  </div>
                  <div className="text-right">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        navigate(`/orders/${order.id}`);
                      }}
                      className="text-primary font-label-sm flex items-center gap-1 hover:text-primary/80 transition-colors"
                    >
                      Подробнее
                      <span className="material-symbols-outlined text-sm">chevron_right</span>
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </main>
  );
};

export default Orders;

