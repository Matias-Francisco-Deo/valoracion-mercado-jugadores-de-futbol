import canchaPelotaBg from '../assets/cancha-pelota.png';
import { LoginForm } from '../components/auth/LoginForm';

export default function LoginPage() {
  return (
    <div
      className="w-full h-[calc(100dvh-4rem)] flex items-center justify-center p-4 bg-center bg-cover overflow-hidden"
      style={{ backgroundImage: `url(${canchaPelotaBg})` }}
    >
      <div className="w-full max-w-md flex justify-center">
        <LoginForm />
      </div>
    </div>
  );
}
