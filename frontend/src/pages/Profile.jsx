import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';

const Profile = () => {
  const { user, logout } = useAuth();
  const [backupCodes, setBackupCodes] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleGenerateBackupCodes = async () => {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const response = await authAPI.generateBackupCodes();
      setBackupCodes(response.data);
      setSuccess('Резервные коды успешно сгенерированы! Сохраните их в безопасном месте.');
    } catch (err) {
      setError('Ошибка при генерации резервных кодов');
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadCodes = () => {
    const text = backupCodes.join('\n');
    const element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', 'backup-codes.txt');
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  };

  return (
    <main className="min-h-screen bg-background py-8 px-4 md:px-8">
      <div className="max-w-6xl mx-auto grid md:grid-cols-3 gap-6">
        {/* Profile Section */}
        <div className="md:col-span-1">
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <div className="flex flex-col items-center space-y-4">
              <div className="relative">
                <img
                  alt="Профиль пользователя"
                  className="w-24 h-24 rounded-full border-4 border-primary-container object-cover shadow-sm"
                  src="https://lh3.googleusercontent.com/aida-public/AB6AXuBku4qlGDr2yKqB6zD11PmxdUlDGN9ed_r4Ilwvp5yIhtVRZfHD57JbSADcjkM40K5HgMCQLJU35IVRA_58Y4mDYmfPGc_fPJkTcRMorxB2lQaYAXLeS0w2l1oaSz5S7ZOvFmOjvobj79bos9_Od8CTEEJIOHWng5_M-yv82Ncbh3nn6FS5eqSGuMtgOpUqoULdayy5F53hI8L3gOtR8r218TyTE1NsSANUY8pCvdOcjO66FHUhXYDA3F-zImX4u6xIoZSzt8VlXKg"
                />
              </div>
              <div className="space-y-1">
                <h2 className="font-headline-md text-on-surface">{user?.email || 'Пользователь'}</h2>
                <p className="font-body-sm text-secondary">{user?.role === 'ADMIN' ? 'Администратор' : 'Пользователь'}</p>
              </div>
              <div className="inline-flex items-center px-3 py-1.5 bg-primary-container/20 border border-primary-container rounded-full">
                <span className="material-symbols-outlined text-primary text-sm mr-1" style={{fontVariationSettings: "'FILL' 1"}}>verified_user</span>
                <span className="font-label-sm text-primary uppercase tracking-wider">Проверен</span>
              </div>
            </div>

            <hr className="my-6 border-surface-container-high" />

            <div className="space-y-3 text-body-sm">
              <div>
                <span className="text-secondary">Email</span>
                <p className="text-on-surface font-semibold mt-1">{user?.email}</p>
              </div>
              <div>
                <span className="text-secondary">Роль</span>
                <p className="text-on-surface font-semibold mt-1">{user?.role === 'ADMIN' ? 'Администратор' : 'Пользователь'}</p>
              </div>
            </div>

            <button
              onClick={logout}
              className="w-full mt-6 flex items-center justify-center gap-2 px-4 py-3 text-error font-body-md hover:bg-error-container/20 rounded-xl transition-colors"
            >
              <span className="material-symbols-outlined">logout</span>
              Выход
            </button>
          </div>
        </div>

        {/* Backup Codes Section */}
        <div className="md:col-span-2">
          <div className="bg-surface-container-lowest p-6 rounded-xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)]">
            <h2 className="font-headline-md text-on-surface mb-4">Резервные коды</h2>
            <p className="font-body-md text-secondary mb-6">
              Генерируйте резервные коды для безопасного входа в аккаунт, если забудете пароль. Каждый код можно использовать только один раз.
            </p>

            {error && (
              <div className="mb-4 p-4 bg-error-container text-error rounded-xl font-body-md">
                {error}
              </div>
            )}

            {success && (
              <div className="mb-4 p-4 bg-green-100 text-green-800 rounded-xl font-body-md">
                ✓ {success}
              </div>
            )}

            {!backupCodes ? (
              <button
                onClick={handleGenerateBackupCodes}
                disabled={loading}
                className="bg-primary-container text-on-primary-fixed px-6 py-3 rounded-full font-label-sm active:scale-95 transition-transform disabled:opacity-60"
              >
                {loading ? 'Генерирование...' : 'Сгенерировать резервные коды'}
              </button>
            ) : (
              <div className="space-y-4">
                <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-xl">
                  <p className="font-label-sm text-yellow-900">
                    <span className="font-bold">⚠️ Важно!</span> Сохраните эти коды в безопасном месте. Каждый код можно использовать только один раз.
                  </p>
                </div>

                <div className="bg-surface-container p-4 rounded-xl overflow-hidden">
                  <div className="overflow-y-auto max-h-64 space-y-2">
                    {backupCodes.map((code, index) => (
                      <div
                        key={index}
                        className="font-mono text-body-sm p-2 bg-surface-container-lowest rounded hover:bg-primary-container/10 cursor-pointer transition-colors"
                      >
                        {code}
                      </div>
                    ))}
                  </div>
                </div>

                <div className="flex gap-3">
                  <button
                    onClick={handleDownloadCodes}
                    className="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-surface-container-high text-on-surface rounded-full font-label-sm hover:bg-surface-container transition-colors"
                  >
                    <span className="material-symbols-outlined">download</span>
                    Скачать коды
                  </button>
                  <button
                    onClick={handleGenerateBackupCodes}
                    className="flex-1 bg-primary-container text-on-primary-fixed px-4 py-3 rounded-full font-label-sm active:scale-95 transition-transform"
                  >
                    Сгенерировать новые
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
};

export default Profile;

