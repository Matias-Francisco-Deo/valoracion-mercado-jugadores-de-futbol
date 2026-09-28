export const LoaderCircle = (props: React.ComponentProps<'svg'>) => {
  return (
    <svg
      aria-label="Cargando..."
      role="img"
      viewBox="0 0 24 24"
      width="24"
      height="24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      {...props}
    >
      <title>Cargando...</title>
      <path d="M21 12a9 9 0 1 1-6.219-8.56" />
    </svg>
  )
}