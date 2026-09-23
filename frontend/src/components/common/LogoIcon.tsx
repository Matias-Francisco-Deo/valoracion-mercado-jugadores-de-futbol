import overlordSkull from "../../../public/overlord-skull.png"

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