import { Button } from "../ui/Button";
import { cn } from "@/lib/utils";
import { getDesktopPages, getMobilePages } from "../../lib/pagination-utils";

interface PaginationProps {
    currentPage: number;
    totalPages: number;
    onPageChange: (page: number) => void;
    className?: string;
}

export const Pagination = ({ currentPage, totalPages, onPageChange, className, ...props }: PaginationProps) => {
    if (totalPages <= 1) {
        return null;
    }

    const goToPrevious = () => {
        if (currentPage > 0) {
            onPageChange(currentPage - 1);
        }
    };

    const goToNext = () => {
        if (currentPage < totalPages - 1) {
            onPageChange(currentPage + 1);
        }
    };

    const desktopPages = getDesktopPages(currentPage, totalPages);
    const mobilePages = getMobilePages(currentPage, totalPages);

    return (
        <nav aria-label="Paginación"
            className={cn("flex items-center justify-center gap-1", className)} {...props}>
            {/* Página anterior */}
            <Button
                onClick={goToPrevious}
                disabled={currentPage === 0}
                aria-label="Página anterior"
                className="flex h-9 w-9 items-center justify-center cursor-pointer disabled:pointer-events-none disabled:opacity-40" >
                ←
            </Button>

            {/* Desktop */}
            <div className="hidden items-center gap-1 md:flex">
                {desktopPages.map((page, index) => {
                    if (page === "...") {
                        return (
                            <span
                                key={`ellipsis-${index}`}
                                className="flex h-9 w-9 items-center justify-center rounded-md bg-brand-orange">
                                ...
                            </span>
                        );
                    }

                    if (page === currentPage) {
                        return (
                            <span
                                key={page}
                                aria-current="page"
                                className="flex h-9 min-w-9 items-center justify-center rounded-md px-2 bg-[#f55e06]">
                                {page + 1}
                            </span>
                        );
                    }

                    return (
                        <Button
                            key={page}
                            onClick={() => onPageChange(page)}
                            aria-current={page === currentPage ? "page" : undefined}
                            className={` flex h-9 min-w-9 items-center justify-center cursor-pointer rounded-md px-2 hover:bg-hover-orange`}>
                            {page + 1}
                        </Button>
                    );
                })}
            </div>

            {/* Mobile */}
            <div className="flex items-center gap-1 md:hidden">
                {mobilePages.map((page) => {

                    if (page === currentPage) {
                        return (
                            <span
                                key={page}
                                aria-current="page"
                                className="flex h-9 min-w-9 items-center justify-center rounded-md px-2 bg-[#f55e06]">
                                {page + 1}
                            </span>
                        );
                    }

                    return (
                        <Button
                            key={page}
                            onClick={() => onPageChange(page)}
                            aria-current={page === currentPage ? "page" : undefined}
                            className={`flex h-9 min-w-9 items-center justify-center cursor-pointer rounded-md px-2`}>
                            {page + 1}
                        </Button>
                    );
                })}
            </div>

            {/* Página siguiente */}
            <Button
                onClick={goToNext}
                disabled={currentPage === totalPages - 1}
                aria-label="Página siguiente"
                className="flex h-9 w-9 items-center justify-center rounded-md cursor-pointer disabled:pointer-events-none disabled:opacity-40">
                →
            </Button>
        </nav>
    );
}
