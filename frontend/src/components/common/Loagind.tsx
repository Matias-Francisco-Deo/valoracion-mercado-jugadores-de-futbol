import { LoaderCircle } from '@/icon/LoaderCircle'
import { cn } from '@/lib/utils'

interface LoadingProps extends React.ComponentProps<'div'> {
  text?: string
}

export const Loading = ({ text, className, ...props }: LoadingProps) => (
  <div
    className={cn('flex h-full flex-col items-center justify-center gap-4', className)}
    {...props}
  >
    <LoaderCircle className="size-12 animate-spin" />
    {text && <span className="text-muted-foreground text-2xl text-center">{text}</span>}
  </div>
)