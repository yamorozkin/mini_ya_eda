import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { itemAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

const CreateItem = () => {
  const { user } = useAuth();
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!name || !price || !imageUrl) {
      setError('Пожалуйста, заполните все поля');
      return;
    }

    setLoading(true);
    try {
      await itemAPI.createItem(name, parseFloat(price), imageUrl);
      setSuccess('Товар успешно добавлен! Переходим на главную страницу...');
      setName('');
      setPrice('');
      setImageUrl('');
      setTimeout(() => navigate('/'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка при добавлении товара');
    } finally {
      setLoading(false);
    }
  };

  if (user?.role !== 'ADMIN') {
    return (
      <main className="max-w-screen-xl mx-auto px-4 py-stack-lg">
        <div className="p-4 bg-error-container text-error rounded-xl font-body-md mb-4">
          У вас нет доступа к этой странице
        </div>
        <button
          onClick={() => navigate('/')}
          className="bg-primary-container text-on-primary-fixed px-6 py-2 rounded-full font-label-sm"
        >
          На главную
        </button>
      </main>
    );
  }

  return (
    <main className="max-w-screen-xl mx-auto px-4 py-stack-lg pb-32">
      <div className="max-w-2xl mx-auto">
        <div className="mb-stack-lg">
          <h1 className="font-headline-xl text-on-surface">Добавить товар</h1>
          <p className="font-body-lg text-secondary">Создайте новый товар для меню</p>
        </div>

        <div className="bg-surface-container-lowest p-8 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
          {error && (
            <div className="mb-stack-md p-4 bg-error-container text-error rounded-xl font-body-md">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-stack-md p-4 bg-green-100 text-green-800 rounded-xl font-body-md">
              ✓ {success}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-stack-md">
            {/* Item Name */}
            <div className="space-y-stack-sm">
              <label className="font-label-sm text-on-surface-variant px-1" htmlFor="name">
                Название товара
              </label>
              <input
                id="name"
                type="text"
                placeholder="Например: Маргарита с пеперони"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                className="w-full bg-surface-container-low border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-4 px-4 font-body-md transition-all outline-none"
              />
              {name && (
                <p className="text-label-sm text-secondary px-1">
                  {name.length} символов
                </p>
              )}
            </div>

            {/* Price */}
            <div className="space-y-stack-sm">
              <label className="font-label-sm text-on-surface-variant px-1" htmlFor="price">
                Цена (₽)
              </label>
              <input
                id="price"
                type="number"
                placeholder="Например: 299.99"
                step="0.01"
                min="0"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                required
                className="w-full bg-surface-container-low border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-4 px-4 font-body-md transition-all outline-none"
              />
              {price && (
                <p className="text-label-sm text-secondary px-1">
                  {parseFloat(price).toFixed(2)} ₽
                </p>
              )}
            </div>

            {/* Image URL */}
            <div className="space-y-stack-sm">
              <label className="font-label-sm text-on-surface-variant px-1" htmlFor="imageUrl">
                URL картинки
              </label>
              <input
                id="imageUrl"
                type="url"
                placeholder="Например: https://example.com/pizza.jpg"
                value={imageUrl}
                onChange={(e) => setImageUrl(e.target.value)}
                required
                className="w-full bg-surface-container-low border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-4 px-4 font-body-md transition-all outline-none"
              />
            </div>

            {/* Image Preview */}
            {imageUrl && (
              <div className="mt-stack-md p-4 bg-surface-container rounded-xl">
                <p className="font-label-sm text-on-surface-variant mb-3">Превью</p>
                <div className="relative w-full h-64 bg-surface-container-low rounded-lg overflow-hidden">
                  <img
                    src={imageUrl}
                    alt="Preview"
                    className="w-full h-full object-cover"
                    onError={(e) => {
                      e.target.src = '';
                      if (e.target.parentElement) {
                        e.target.parentElement.innerHTML = '<div class="flex items-center justify-center h-full text-error"><span class="material-symbols-outlined text-6xl">image_not_supported</span></div>';
                      }
                    }}
                  />
                </div>
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading || !name || !price || !imageUrl}
              className="w-full bg-primary-container text-on-primary-fixed font-headline-md py-4 rounded-full shadow-[0px_4px_20px_rgba(255,210,0,0.3)] hover:shadow-[0px_8px_30px_rgba(255,210,0,0.4)] active:scale-95 transition-all duration-200 flex items-center justify-center gap-2 disabled:opacity-60 mt-stack-lg"
            >
              {loading ? (
                <>
                  <span className="animate-spin material-symbols-outlined">hourglass_top</span>
                  Добавление...
                </>
              ) : (
                <>
                  <span className="material-symbols-outlined">add</span>
                  Добавить товар
                </>
              )}
            </button>

            {/* Back Button */}
            <button
              type="button"
              onClick={() => navigate('/')}
              className="w-full text-primary font-label-sm py-3 hover:bg-surface-container rounded-full transition-colors"
            >
              Отмена
            </button>
          </form>
        </div>
      </div>
    </main>
  );
};

export default CreateItem;

