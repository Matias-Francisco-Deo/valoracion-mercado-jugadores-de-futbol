import { cn } from '@/lib/utils'

export const Input = ({ className, ...props }: React.ComponentProps<'input'>) => {
  return (
    <input
    autoCorrect='none'
      className={cn(
        'border-border w-full rounded border px-4 py-2 focus:outline-none ',

        'placeholder:text-input-foreground',

        'user-invalid:border-destructive',
        'user-invalid:focus:outline-destructive',
        'user-invalid:placeholder:text-destructive',

        className,
      )}
      {...props}
    />
  )
}