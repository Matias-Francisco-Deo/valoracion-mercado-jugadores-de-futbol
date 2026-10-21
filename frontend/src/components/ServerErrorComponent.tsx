interface ServerErrorComponentProps extends React.ComponentProps<'div'> {
  title?:string;
  text?: string;
}

export const ServerErrorComponent = ({title, text, ...props}:ServerErrorComponentProps) => {
  return (
    <div className="flex flex-1 w-full items-center justify-center" {...props}>
        <div className="flex flex-col max-w-125 gap-4 bg-[#d9d9d9af] p-4 rounded">
          <div className="text-2xl font-bold text-center">
            {title ?? "Hubo un error inesperado"}
          </div>
          <div className="font-bold text-center">
            {text ?? "Estamos solucionando el problema. Estaremos de vuelta en breve."}
          </div>
        </div>
    </div>
    
  )
}