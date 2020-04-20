package com.simplepos.di

import android.app.Application
import androidx.room.Room
import com.simplepos.data.*
import com.simplepos.orders.ScanProductViewModel
import com.simplepos.orders.OrderViewModel
import com.simplepos.orders.SearchProductViewModel
import com.simplepos.products.*
import com.simplepos.reports.ReportViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val posModule= module {
    fun provideDatabase(app : Application) : PosDatabase{
        return Room.databaseBuilder(
            app,
            PosDatabase::class.java, "posdb"
        ).build()
    }
    fun provideProductDao(database: PosDatabase):ProductDao{
        return database.productDao
    }
    fun provideOrderDao(database: PosDatabase):OrderDao{
        return database.orderDao
    }
    fun provideSalesDao(database: PosDatabase):SalesDao{
        return database.salesDao
    }
    fun provideProductRepository(productDao : ProductDao):ProductRepository{
        return ProductRepository(productDao)
    }
    fun provideOrderRepository(orderDao: OrderDao):OrderRepository{
        return OrderRepository(orderDao)
    }
    fun provideSalesRepository(salesDao: SalesDao):SalesRepository{
        return SalesRepository(salesDao)
    }
    fun provideOrderViewModel(orderRepository: OrderRepository, salesRepository: SalesRepository, productRepository: ProductRepository): OrderViewModel {
        return OrderViewModel(orderRepository,salesRepository,productRepository)
    }
    fun provideScanProductViewModel(productRepository: ProductRepository): ScanProductViewModel {
        return ScanProductViewModel(productRepository)
    }
    fun provideScanBarcodeViewModel(): ScanBarcodeViewModel {
        return ScanBarcodeViewModel()
    }
    fun provideProductViewModel(productRepository: ProductRepository): ProductViewModel {
        return ProductViewModel(productRepository)
    }

    fun provideProductEditViewModel(productRepository: ProductRepository): ProductEditViewModel {
        return ProductEditViewModel(productRepository)
    }
    fun provideProductAddViewModel(productRepository: ProductRepository): ProductAddViewModel {
        return ProductAddViewModel(productRepository)
    }
    fun provideSearchProductViewModel(productRepository: ProductRepository): SearchProductViewModel {
        return SearchProductViewModel(productRepository)
    }

    fun provideReportViewModel(salesRepository: SalesRepository):ReportViewModel{
        return ReportViewModel(salesRepository)
    }


    single<PosDatabase> { provideDatabase(get()) }
    single<ProductDao> { provideProductDao(get()) }
    single<OrderDao> { provideOrderDao(get()) }
    single<SalesDao> { provideSalesDao(get()) }
    single<ProductRepository> {provideProductRepository(get())}
    single<SalesRepository> {provideSalesRepository(get())}
    single<OrderRepository> {provideOrderRepository(get())}
    single<OrderViewModel> {provideOrderViewModel(get(),get(),get())}
    single<ProductViewModel> {provideProductViewModel(get())}
    single<ProductEditViewModel> {provideProductEditViewModel(get())}
    viewModel<ScanProductViewModel> {provideScanProductViewModel(get())}
    single<ScanBarcodeViewModel> { provideScanBarcodeViewModel()}
    single<ProductAddViewModel> { provideProductAddViewModel(get())}
    viewModel<SearchProductViewModel> { provideSearchProductViewModel(get())}
    viewModel<ReportViewModel> { provideReportViewModel(get())}
}




