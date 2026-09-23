import {Link} from "react-router-dom";
import type {LinkProps} from "react-router";
import {cn} from "@/lib/utils.ts";

export const PageLink = ({ className, children, ...props }: LinkProps) => {
  return (
      <div >
          <Link
              className={cn(
                  'bg-brand-orange text-primary-foreground rounded-lg px-4 py-2 transition',

                  'enabled:hover:opacity-90 enabled:active:scale-95',

                  'disabled:cursor-not-allowed disabled:opacity-70', className
              )}
              {...props}
          >
              {children}
          </Link>
      </div>
  )
}