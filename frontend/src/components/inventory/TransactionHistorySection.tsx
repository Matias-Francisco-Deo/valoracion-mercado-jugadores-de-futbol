import type { PageResponse, Transaction } from "@/types/inventory";
import type { ComponentProps } from "react";
import { Pagination } from "../common/Pagination";
import type { HttpError } from "@/lib/http-error";
import { formatTransactionDate } from "@/lib/utils";

const transactionTypeConfig: Record<Transaction["type"],{ label: string; className: string }> = {
    BUY: {
        label: "Compra",
        className: "bg-orange-100 text-orange-700",
    },
    SELL: {
        label: "Venta",
        className: "bg-green-100 text-green-700",
    },
};

type  TransactionHistorySectionprops = ComponentProps<"div"> & {
    transactionsPage:PageResponse<Transaction> |null;
    onPageChange: (page: number) => void;
    transactionError:HttpError | null;
}

export const TransactionHistorySection = ({ transactionsPage,onPageChange,transactionError, ...props }: TransactionHistorySectionprops) => {
    return(
        <div className="flex w-full flex-col gap-4 rounded-2xl bg-[#f3f1f1] p-4 shadow-sm sm:p-6 text-black" {...props}>
            <div className="flex flex-col">
                <span className="text-[9px] font-bold tracking-widest text-brand-orange">ACTIVIDAD</span>
                <h2 className="text-xl font-medium">Historial de transacciones</h2>
            </div>
            <div className="border border-gray-300 rounded-2xl overflow-x-auto">
                <table className="w-full whitespace-nowrap border-collapse [&_th]:p-2 [&_th]:text-[9px] [&_th]:text-gray-400 [&_th]:text-start [&_td]:border-t [&_td]:border-gray-300 [&_td]:text-xs [&_td]:p-2 [&_td]:border-collapse [&_td]:text-start [&_td]:font-titles" > 
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
                        {transactionsPage === null || transactionError ? (
                                <tr>
                                    <td colSpan={6} className="py-6 text-center! text-gray-500">
                                        Error al cargar las Transacciones.
                                    </td>
                                </tr>
                            ) : transactionsPage.content.length > 0 ? (
                                transactionsPage.content.map((transaction) => (
                                    <tr key={transaction.id}>
                                        <td>{formatTransactionDate(transaction.date)}</td>
                                        <td>{transaction.playerName}</td>
                                        <td>
                                            <span className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${transactionTypeConfig[transaction.type].className}`}>
                                                {transactionTypeConfig[transaction.type].label}
                                            </span>
                                        </td>
                                        <td>{transaction.quantity}</td>
                                        <td>${transaction.unitPrice}</td>
                                        <td>${transaction.totalPrice}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan={6} className="py-6 text-center! text-gray-500">
                                        No hay transacciones
                                    </td>
                                </tr>
                            )}
                    </tbody>
                </table>
                <Pagination 
                    currentPage={transactionsPage?.number ?? 0}
                    totalPages={transactionsPage?.totalPages ?? 0}
                    onPageChange={onPageChange}/>
            </div>
        </div>
    )
}