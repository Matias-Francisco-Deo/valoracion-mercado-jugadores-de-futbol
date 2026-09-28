import overlordSkull from '@/assets/overlord-skull.png';

export const LogoIcon = ({ className, ...props }: React.ComponentProps<'img'>) => {
  return (
    <img
      src={overlordSkull}
      alt="Overlord logo"
      className={`w-12 h-12 object-contain ${className}`}
      {...props}
    />
  );
}