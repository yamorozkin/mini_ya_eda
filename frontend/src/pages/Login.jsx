import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await authAPI.login(email, password);
      const token = response.data.token;

      const payload = JSON.parse(atob(token.split('.')[1]));
      login(token, {
        email: payload.sub,
        role: payload.role
      });

      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка входа. Проверьте учетные данные.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col md:flex-row bg-background">
      {/* Brand Visual Section */}
      <section className="relative w-full md:w-1/2 lg:w-3/5 min-h-[300px] md:min-h-screen bg-neutral-900 overflow-hidden flex items-center justify-center">
        <img
          className="absolute inset-0 w-full h-full object-cover opacity-60"
          alt="Food imagery"
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuAN4DP8K6_swoH0zOPDpdtRq84J_N96SV_0NqUDMxMLmZSoxilOcZsPlsx7Yt2aIeBaFrtg1BBNV9_EtKxcxNz_T2liAfHEo6Z0coKXzbAIePW-GQBgaoqYN6rlpmrcK5owG1hac5zSm1bB35Bp3rMt47ZgHxKVyLseK13CafYOA5ShjQ0xosY3wwUancK2qkDfm2pQzcKp5hPf0WrsCFaANAKpXgi22JCamUTpvrTcksWEnueA_AO_IeEIVk6SCwnPkEwQu3jfji0"
        />
        <div className="relative z-10 p-margin-page text-center md:text-left max-w-lg">
          <h1 className="font-headline-xl text-white mb-stack-sm">Burger House</h1>
          <p className="font-body-lg text-white/80">Быстрая доставка вкусных бургеров для занятых людей. Наши бургеры приготовлены с любовью и доставлены за 30 минут.</p>
          <div className="hidden md:grid grid-cols-2 gap-stack-md mt-stack-lg">
            <div className="bg-white/10 backdrop-blur-md p-stack-md rounded-xl border border-white/10">
              <span className="material-symbols-outlined text-primary-container mb-stack-sm">bolt</span>
              <h3 className="font-headline-md text-white">Быстрая доставка</h3>
              <p className="text-white/60 text-label-sm">Среднее время доставки 25 минут</p>
            </div>
            <div className="bg-white/10 backdrop-blur-md p-stack-md rounded-xl border border-white/10">
              <span className="material-symbols-outlined text-primary-container mb-stack-sm">restaurant</span>
              <h3 className="font-headline-md text-white">Проверенные</h3>
              <p className="text-white/60 text-label-sm">Лучшие рестораны вашего города</p>
            </div>
          </div>
        </div>
      </section>

      {/* Auth Form Section */}
      <main className="w-full md:w-1/2 lg:w-2/5 flex flex-col justify-center p-margin-page">
        <div className="max-w-md w-full mx-auto">
          <div className="mb-stack-lg text-center md:text-left">
            <h2 className="font-headline-xl text-on-surface">Добро пожаловать</h2>
            <p className="font-body-lg text-secondary">Введите свои данные для входа</p>
          </div>

          {error && (
            <div className="mb-stack-md p-4 bg-error-container text-error rounded-xl font-body-md">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-stack-md">
            <div className="space-y-stack-sm">
              <label className="font-label-sm text-on-surface-variant px-1" htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="w-full bg-surface-container-lowest border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-4 px-4 font-body-md transition-all outline-none"
                placeholder="your@email.com"
              />
            </div>

            <div className="space-y-stack-sm">
              <label className="font-label-sm text-on-surface-variant px-1" htmlFor="password">Пароль</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full bg-surface-container-lowest border-none ring-1 ring-surface-container-high focus:ring-2 focus:ring-primary-container rounded-xl py-4 px-4 font-body-md transition-all outline-none"
                placeholder="Ваш пароль"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary-container text-on-primary-fixed font-headline-md py-4 rounded-full shadow-[0px_4px_20px_rgba(255,210,0,0.3)] hover:shadow-[0px_8px_30px_rgba(255,210,0,0.4)] active:scale-95 transition-all duration-200 flex items-center justify-center gap-2 disabled:opacity-60"
            >
              {loading ? 'Загрузка...' : 'Войти'}
              <span className="material-symbols-outlined">arrow_forward</span>
            </button>
          </form>

          <p className="mt-stack-lg text-center font-label-sm text-secondary">
            Нет аккаунта? <br/>
            <a href="/register" className="text-on-surface font-bold hover:underline">Зарегистрироваться</a>
          </p>
        </div>
      </main>
    </div>
  );
};

export default Login;
