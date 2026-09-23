import canchaPelotaBg from '../assets/cancha-pelota.png';
import { RegisterForm } from '../components/auth/RegisterForm';

export default function RegisterPage() {
  return (
      <div className=" w-full h-[calc(100dvh-4rem)] flex items-center justify-center p-4 bg-center bg-cover overflow-hidden"
    style={{ backgroundImage: `url(${canchaPelotaBg})` }}>


      {/* Centered Registration Card Container */}
      <div className="w-full max-w-md flex justify-center">
        <RegisterForm />
      </div>
    </div>
  );
};
