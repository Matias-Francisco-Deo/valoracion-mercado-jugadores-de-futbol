import {Button} from "@/components/ui/Button.tsx";
import {Link} from "react-router-dom";
import type {LinkProps} from "react-router";

export const PageLink = ({ className, ...props }: LinkProps) => {
  return (
      <div >
          <Button className="bg-brand-orange max-w-30 text-lg">
              <Link
                    className=""
                    {...props}
              >
                  Ver más
              </Link>
          </Button>
      </div>
  )
}