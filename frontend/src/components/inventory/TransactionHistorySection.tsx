import type { Transaction } from "@/types/inventory";
import type { ComponentProps } from "react";

type  TransactionHistorySectionprops = ComponentProps<"div"> & {
    transactions:Transaction[];
}

export const TransactionHistorySection = ({ transactions, className, ...props }: TransactionHistorySectionprops) => {
    return(
        <div className="flex w-full flex-col gap-4 rounded-2xl bg-[#f3f1f1] p-4 shadow-sm sm:p-6 text-black">
            <div className="flex flex-col">
                <span className="text-[9px] font-bold tracking-widest text-brand-orange">ACTIVIDAD</span>
                <h2 className="text-xl font-medium">Historial de transacciones</h2>
            </div>
            <div className="border border-gray-300 rounded-2xl overflow-x-auto">
                <table className="w-full whitespace-nowrap border-collapse [&_th]:p-2 [&_th]:text-[9px] [&_th]:text-gray-400 [&_th]:text-start [&_td]:border-t [&_td]:border-gray-300 [&_td]:text-xs [&_td]:p-2 [&_td]:border-collapse" > 
                    <thead className="bg-[#f1e0d2]">
                        <tr>
                            <th>FECHA</th>
                            <th>JUGADOR</th>
                            <th>TIPO</th>
                            <th>TOKENS</th>
                            <th>P/U</th>
                            <th>TOTAL</th>
                        </tr>
                    </thead>
                    <tbody>
                        {transactions.length > 0 ? (transactions.map((transaction)=>(
                            <tr key={transaction.id}>
                                <td>{transaction.date}</td>
                                <td>{transaction.playerName}</td>
                                <td>{transaction.type}</td>
                                <td>{transaction.quantity}</td>
                                <td>{transaction.unitPrice}</td>
                                <td>{transaction.totalAmount}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={6} className="py-6 text-center text-gray-500">
                                No hay transacciones
                            </td>
                        </tr>
                    )}
                    </tbody>
                    
                </table>
            </div>
        </div>
    )
}